const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Triggers upon new user registration via Firebase Auth.
 * Automatically generates a welcome notification in Firestore and dispatches
 * a branded Haven International Realty welcome email.
 */
exports.sendWelcomeEmailOnRegistration = functions.auth.user().onCreate(async (user) => {
  const email = user.email;
  const displayName = user.displayName || "Valued Client";
  const uid = user.uid;

  functions.logger.info(`New user registered on Haven: ${email} (UID: ${uid})`);

  const brandedWelcomeHtml = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <title>Welcome to Haven International Realty</title>
      <style>
        body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #FAFAF8; margin: 0; padding: 40px 20px; color: #1F2937; }
        .container { max-width: 600px; margin: 0 auto; background-color: #FFFFFF; border: 1px solid #E5E7EB; border-radius: 4px; overflow: hidden; }
        .header { background-color: #080E1A; padding: 36px 30px; text-align: center; border-bottom: 2px solid #C5A880; }
        .logo { font-size: 26px; font-weight: bold; letter-spacing: 5px; color: #FFFFFF; margin: 0; }
        .sublogo { font-size: 10px; font-weight: 600; letter-spacing: 2px; color: #C5A880; margin-top: 6px; }
        .content { padding: 40px 36px; }
        .salutation { font-size: 20px; font-weight: bold; color: #080E1A; margin-bottom: 16px; }
        .text { font-size: 14px; line-height: 1.7; color: #4B5563; margin-bottom: 20px; }
        .feature-box { background-color: #F9F8F5; border-left: 3px solid #C5A880; padding: 18px 20px; margin: 26px 0; }
        .feature-title { font-size: 13px; font-weight: bold; color: #080E1A; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 6px; }
        .feature-desc { font-size: 12.5px; color: #4B5563; line-height: 1.5; margin: 0; }
        .cta-btn { display: inline-block; background-color: #080E1A; color: #C5A880; text-decoration: none; padding: 14px 28px; font-size: 12px; font-weight: bold; letter-spacing: 1.5px; text-transform: uppercase; border-radius: 2px; border: 1px solid #C5A880; margin: 20px 0; }
        .footer { background-color: #F4F4F1; padding: 24px 30px; text-align: center; font-size: 11px; color: #9CA3AF; line-height: 1.6; }
      </style>
    </head>
    <body>
      <div class="container">
        <div class="header">
          <h1 class="logo">H A V E N</h1>
          <div class="sublogo">INTERNATIONAL REALTY</div>
        </div>
        <div class="content">
          <div class="salutation">Welcome to Haven, ${displayName}.</div>
          <p class="text">
            Thank you for creating your private account with Haven International Realty. You now have privileged access to our curated portfolio of ultra-prime residences, waterfront penthouses, and private estates across the world's premier markets.
          </p>

          <div class="feature-box">
            <div class="feature-title">Verified Institutional Legal Deeds</div>
            <p class="feature-desc">Every acquisition opportunity on Haven has undergone independent title auditing (C of O, Governor's Consent, HM Land Registry, and Dubai Land Department) before client presentation.</p>
          </div>

          <div class="feature-box">
            <div class="feature-title">Global Portfolio Markets</div>
            <p class="feature-desc">Active listings and confidential private inventory spanning Nigeria (Banana Island & Maitama), the United Kingdom (Mayfair), the United Arab Emirates (Palm Jumeirah), and the United States (Miami).</p>
          </div>

          <div class="feature-box">
            <div class="feature-title">Dedicated Private Advisory</div>
            <p class="feature-desc">Direct, confidential 1-on-1 communication with senior brokers and conveyancers to arrange private physical or virtual viewings at your convenience.</p>
          </div>

          <center>
            <a href="https://haven-realty.app" class="cta-btn">Access Prime Portfolio</a>
          </center>

          <p class="text" style="font-size: 12px; color: #9CA3AF; margin-top: 30px;">
            If you did not initiate this account creation, please contact our confidential client desk immediately at privateclients@haven-realty.com.
          </p>
        </div>
        <div class="footer">
          HAVEN INTERNATIONAL REALTY • PRIVILEGED CLIENT NETWORK<br>
          Offices: Lagos • London • Dubai • New York • Miami<br>
          © ${new Date().getFullYear()} Haven International Realty. Equal Housing Opportunity.
        </div>
      </div>
    </body>
    </html>
  `;

  // 1. Create client in-app welcome notification & record in Firestore
  const notificationPayload = {
    userId: uid,
    title: "Welcome to Haven International Realty",
    message: `Welcome ${displayName}! Your private acquisition account has been registered with access to our global prime estate catalog.`,
    type: "WELCOME_NOTIFICATION",
    read: false,
    emailSentTo: email,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  };

  const db = admin.firestore();
  await db.collection("users").doc(uid).collection("notifications").add(notificationPayload);

  // 2. Queue email in Firestore mail collection (integrates with Firebase Extension: Trigger Email with SendGrid / SMTP / Mailgun)
  if (email) {
    await db.collection("mail").add({
      to: [email],
      message: {
        subject: "Welcome to Haven International Realty — Private Client Portfolio Access",
        text: `Welcome to Haven, ${displayName}. Your private client account is now active. Explore prime estates across Nigeria, the US, the UK, and UAE with verified legal titles.`,
        html: brandedWelcomeHtml
      },
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });
  }

  functions.logger.info(`Successfully recorded welcome notification and queued branded welcome email for ${email}`);
  return { success: true, email: email };
});
