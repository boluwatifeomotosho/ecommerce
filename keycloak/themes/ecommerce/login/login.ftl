<#ftl output_format="HTML">
<!DOCTYPE html>
<html lang="${locale.currentLanguageTag!'en'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In | JustJava</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(135deg, #1a0a00 0%, #3d2000 50%, #6b3a1f 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            color: #1f2937;
            overflow: hidden;
        }

        /* ── Orbs ── */
        .orb {
            position: fixed;
            border-radius: 50%;
            filter: blur(80px);
            opacity: .25;
            pointer-events: none;
            animation: orbFloat 8s ease-in-out infinite;
        }
        .orb-1 { width:360px; height:360px; background:#c4622d; top:-80px; right:-80px; animation-delay:0s; }
        .orb-2 { width:280px; height:280px; background:#6b3a1f; bottom:-60px; left:-60px; animation-delay:-3s; }
        .orb-3 { width:180px; height:180px; background:#e8a87c; top:50%; left:10%; animation-delay:-5s; }

        @keyframes orbFloat {
            0%,100% { transform: translate(0,0) scale(1); }
            33%  { transform: translate(20px,-20px) scale(1.05); }
            66%  { transform: translate(-15px,10px) scale(.95); }
        }

        /* ── Card entrance ── */
        @keyframes cardIn {
            from { opacity:0; transform: translateY(32px) scale(.97); }
            to   { opacity:1; transform: translateY(0)   scale(1); }
        }
        @keyframes fadeIn { from{opacity:0;} to{opacity:1;} }
        @keyframes badgePulse { 0%,100%{transform:scale(1);opacity:1;} 50%{transform:scale(1.5);opacity:.5;} }

        .card {
            background: white;
            border-radius: 20px;
            padding: 44px 40px;
            width: 100%;
            max-width: 420px;
            box-shadow: 0 32px 80px rgba(0,0,0,.5), 0 0 0 1px rgba(255,255,255,.05);
            animation: cardIn .7s cubic-bezier(.22,1,.36,1) both;
            position: relative;
            z-index: 1;
        }

        .brand { text-align: center; margin-bottom: 28px; animation: fadeIn .5s .2s both; }

        .brand-mark {
            width: 60px; height: 60px;
            background: linear-gradient(135deg, #6b3a1f, #c4622d);
            border-radius: 16px;
            display: inline-flex; align-items: center; justify-content: center;
            margin-bottom: 14px;
            box-shadow: 0 8px 24px rgba(196,98,45,.4);
        }
        .brand-mark svg { width: 32px; height: 32px; fill: white; }
        .brand h1 { font-size: 22px; font-weight: 700; color: #1a0a00; letter-spacing: -.5px; }
        .brand p  { font-size: 13px; color: #9ca3af; margin-top: 4px; }

        h2 { font-size: 20px; font-weight: 600; color: #1a0a00; margin-bottom: 6px; text-align: center; animation: fadeIn .5s .25s both; }
        .subtitle { text-align:center; font-size:14px; color:#6b7280; margin-bottom:24px; animation: fadeIn .5s .3s both; }

        .alert { padding:12px 14px; border-radius:8px; margin-bottom:18px; font-size:13px; line-height:1.4; animation: fadeIn .4s both; }
        .alert-error   { background:#fef2f2; border:1px solid #fecaca; color:#b91c1c; }
        .alert-warning { background:#fffbeb; border:1px solid #fde68a; color:#b45309; }
        .alert-success { background:#f0fdf4; border:1px solid #bbf7d0; color:#15803d; }
        .alert-info    { background:#eff6ff; border:1px solid #bfdbfe; color:#1d4ed8; }

        .field { margin-bottom:16px; animation: fadeIn .5s both; }
        .field:nth-child(1){ animation-delay:.35s; }
        .field:nth-child(2){ animation-delay:.42s; }

        label { display:block; font-size:13px; font-weight:500; color:#374151; margin-bottom:6px; }

        .input-wrap { position:relative; }

        input[type="email"],
        input[type="password"],
        input[type="text"] {
            width:100%; padding:11px 14px;
            border:1.5px solid #d1d5db; border-radius:8px;
            font-size:14px; font-family:inherit; color:#111827;
            background:#f9fafb; outline:none;
            transition: border-color .15s, background .15s, box-shadow .15s;
        }
        input:focus { border-color:#6b3a1f; background:white; box-shadow:0 0 0 3px rgba(107,58,31,.12); }
        .input-wrap input { padding-right: 42px; }

        .toggle-pwd {
            position:absolute; right:10px; top:50%; transform:translateY(-50%);
            background:none; border:none; cursor:pointer; padding:6px; color:#9ca3af;
            display:flex; align-items:center; border-radius:4px;
            transition: color .15s, background .15s;
        }
        .toggle-pwd:hover { color:#6b3a1f; background:#f3f4f6; }
        .toggle-pwd svg { width:18px; height:18px; }

        .row { display:flex; align-items:center; justify-content:space-between; margin:18px 0; gap:12px; animation: fadeIn .5s .5s both; }

        .checkbox-label {
            display:flex; align-items:center; gap:8px; font-size:13px;
            color:#6b7280; cursor:pointer; font-weight:400; user-select:none; margin:0;
        }
        .checkbox-label input[type="checkbox"] { width:15px; height:15px; accent-color:#6b3a1f; cursor:pointer; }

        .link { font-size:13px; color:#6b3a1f; text-decoration:none; font-weight:600; }
        .link:hover { text-decoration:underline; }

        .btn {
            width:100%; padding:12px;
            background: linear-gradient(135deg, #6b3a1f, #c4622d);
            color:white; border:none; border-radius:8px;
            font-size:14px; font-weight:600; font-family:inherit;
            cursor:pointer; letter-spacing:.2px;
            transition: opacity .15s, transform .1s, box-shadow .2s;
            box-shadow: 0 4px 16px rgba(196,98,45,.3);
            animation: fadeIn .5s .55s both;
        }
        .btn:hover { opacity:.92; box-shadow:0 8px 24px rgba(196,98,45,.4); }
        .btn:active { transform:scale(.99); }

        .footer { text-align:center; font-size:13px; color:#6b7280; margin-top:22px; animation: fadeIn .5s .65s both; }
        .footer a { color:#6b3a1f; font-weight:600; text-decoration:none; }
        .footer a:hover { text-decoration:underline; }

        @media (max-width:480px) { .card { padding:32px 22px; } }
    </style>
</head>
<body>
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>
    <div class="orb orb-3"></div>

    <div class="card">
        <div class="brand">
            <div class="brand-mark">
                <svg viewBox="0 0 24 24"><path d="M4 19h16v2H4v-2zm15-9h1c1.1 0 2 .9 2 2v3c0 1.1-.9 2-2 2h-1.32A4.99 4.99 0 0 1 14 21H8a5 5 0 0 1-5-5V4h16v6zm0 5h1v-3h-1v3z"/></svg>
            </div>
            <h1>JustJava</h1>
            <p>Premium coffee, delivered.</p>
        </div>

        <h2>Welcome back</h2>
        <p class="subtitle">Sign in to your account to continue</p>

        <#if message?has_content>
            <div class="alert alert-${message.type}">
                ${kcSanitize(message.summary)?no_esc}
            </div>
        </#if>

        <form action="${url.loginAction}" method="post" novalidate>
            <div class="field">
                <label for="username">Email address</label>
                <input id="username" name="username" type="email" autocomplete="email"
                    autofocus required value="${(login.username!'')}" placeholder="you@example.com"/>
            </div>

            <div class="field">
                <label for="password">Password</label>
                <div class="input-wrap">
                    <input id="password" name="password" type="password"
                        autocomplete="current-password" required placeholder="Enter your password"/>
                    <button type="button" class="toggle-pwd" id="togglePwd" aria-label="Show password">
                        <svg id="eyeIcon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                            <circle cx="12" cy="12" r="3"/>
                        </svg>
                    </button>
                </div>
            </div>

            <div class="row">
                <#if realm.rememberMe>
                    <label class="checkbox-label" for="rememberMe">
                        <input type="checkbox" id="rememberMe" name="rememberMe" <#if login.rememberMe??>checked</#if>>
                        Remember me
                    </label>
                <#else><span></span></#if>
                <#if realm.resetPasswordAllowed>
                    <a href="${url.loginResetCredentialsUrl}" class="link">Forgot password?</a>
                </#if>
            </div>

            <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>>
            <button type="submit" class="btn" name="login">Sign in</button>
        </form>

        <#if realm.registrationAllowed>
            <div class="footer">Don't have an account? <a href="${url.registrationUrl}">Create one</a></div>
        </#if>
    </div>

    <script>
        (function () {
            var toggle = document.getElementById('togglePwd');
            var pwd    = document.getElementById('password');
            var eye    = document.getElementById('eyeIcon');
            var EYE_OPEN = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>';
            var EYE_OFF  = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>';
            toggle.addEventListener('click', function () {
                var showing = pwd.type === 'text';
                pwd.type = showing ? 'password' : 'text';
                eye.innerHTML = showing ? EYE_OPEN : EYE_OFF;
                toggle.setAttribute('aria-label', showing ? 'Show password' : 'Hide password');
            });
        })();
    </script>
</body>
</html>
