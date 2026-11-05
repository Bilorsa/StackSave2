require("dotenv").config();
const express = require("express");
const admin = require("firebase-admin");
const fs = require("fs");
const rateLimit = require("express-rate-limit");

const app = express();
const PORT = Number(process.env.PORT || 8080);
const AZ_LABS_API_KEY = process.env.AZ_LABS_API_KEY;
const AZ_LABS_BASE_URL = (process.env.AZ_LABS_BASE_URL || "https://azlabs.ai").replace(/\/$/, "");
app.disable("x-powered-by");
app.use(express.json({ limit: "32kb" }));

if (!admin.apps.length) {
  const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT_JSON;
  if (serviceAccountPath) {
    if (!fs.existsSync(serviceAccountPath)) throw new Error("Firebase service-account file not found at FIREBASE_SERVICE_ACCOUNT_JSON path.");
    let serviceAccount;
    try { serviceAccount = JSON.parse(fs.readFileSync(serviceAccountPath, "utf8")); }
    catch { throw new Error("Firebase service-account file is not valid JSON."); }
    admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
  } else {
    admin.initializeApp({ credential: admin.credential.applicationDefault() });
  }
}

const limiter = rateLimit({ windowMs: 60 * 1000, limit: 30, standardHeaders: "draft-7", legacyHeaders: false });
async function requireFirebaseUser(req, res, next) {
  const header = req.get("Authorization") || "";
  const token = header.startsWith("Bearer ") ? header.slice(7).trim() : "";
  if (!token) return res.status(401).json({ error: "Firebase sign-in is required." });
  try { req.firebaseUser = await admin.auth().verifyIdToken(token); return next(); }
  catch { return res.status(401).json({ error: "Firebase ID token is invalid, expired, or from another Firebase project." }); }
}

app.get("/health", (_req, res) => res.json({ ok: true, service: "stacksave-azlabs-proxy", firebaseAdmin: admin.apps.length > 0, azLabsKeyConfigured: Boolean(AZ_LABS_API_KEY) }));

app.get("/api/deals", limiter, requireFirebaseUser, async (req, res) => {
  if (!AZ_LABS_API_KEY) return res.status(503).json({ error: "Server is missing AZ_LABS_API_KEY. Configure backend/.env." });
  const search = String(req.query.search || "").trim().slice(0, 80);
  if (!search) return res.status(400).json({ error: "A search term is required, for example ?search=bread." });
  const limit = Math.min(50, Math.max(1, Number.parseInt(req.query.limit, 10) || 20));
  const requests = [
    { path: "/api/v1/grocery/compare", params: { q: search, limit: String(Math.min(limit, 10)) } },
    { path: "/api/v1/grocery/search", params: { q: search, store: "all", limit: String(limit) } }
  ];
  let lastStatus = 502;
  for (const request of requests) {
    const url = new URL(AZ_LABS_BASE_URL + request.path);
    Object.entries(request.params).forEach(([key, value]) => url.searchParams.set(key, value));
    try {
      const upstream = await fetch(url, {
        headers: { Authorization: `Bearer ${AZ_LABS_API_KEY}`, Accept: "application/json" },
        signal: AbortSignal.timeout(20000)
      });
      const raw = await upstream.text();
      let payload;
      try { payload = JSON.parse(raw); } catch { payload = { raw }; }
      if (!upstream.ok) {
        lastStatus = upstream.status;
        // Do not expose the upstream credential or full request URL.
        if (upstream.status === 401 || upstream.status === 403 || upstream.status === 429) {
          return res.status(upstream.status).json({ error: "AZ Labs rejected or rate-limited the request.", upstreamStatus: upstream.status });
        }
        continue;
      }
      if (hasOffers(payload)) return res.status(200).json(payload);
    } catch (error) {
      console.error("AZ Labs request failed:", error.message);
      lastStatus = 502;
    }
  }
  if (lastStatus !== 502) return res.status(lastStatus).json({ error: "AZ Labs request failed.", upstreamStatus: lastStatus });
  return res.status(200).json({ data: [], message: "AZ Labs returned no matching offers." });
});

function hasOffers(value) {
  if (!value || typeof value !== "object") return false;
  if (Array.isArray(value)) return value.length > 0;
  return Object.entries(value).some(([key, item]) => key.toLowerCase() !== "meta" && key.toLowerCase() !== "pagination" && hasOffers(item));
}

app.listen(PORT, "0.0.0.0", () => console.log(`StackSave AZ Labs proxy listening on port ${PORT}`));
