<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
<#if section = "header">
    <title>Pinepetosan Marketplace</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="preconnect" href="https://fonts.gstatic.com/" crossorigin />
    <link rel="stylesheet" as="style" onload="this.rel='stylesheet'"
          href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap"/>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <style>
        html, body { margin: 0; padding: 0; background: transparent; }

        .login-pf-page-header,
        .login-pf-header,
        h1#kc-page-title,
        .login-pf-signup { display: none !important; }
        .login-pf, .login-pf-page, .login-pf-body {
            background: transparent !important;
            padding: 0 !important;
            margin: 0 !important;
            border: none !important;
            min-height: 0 !important;
        }
        .login-pf-page .card-pf,
        #kc-content, #kc-content-wrapper {
            padding: 0 !important;
            margin: 0 !important;
            border: none !important;
            max-width: none !important;
            width: 100% !important;
            background: transparent !important;
            box-shadow: none !important;
        }

        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }

        .pmk-info-shell {
            position: fixed;
            inset: 0;
            z-index: 9999;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1.5rem;
            background: linear-gradient(135deg, #faf5ff 0%, #eef2ff 100%);
            font-family: 'Plus Jakarta Sans', sans-serif;
            overflow: auto;
        }
        .pmk-orb {
            position: absolute;
            border-radius: 9999px;
            opacity: 0.25;
            filter: blur(4px);
            pointer-events: none;
        }
        .pmk-orb-1 { top: -80px; left: -80px; width: 260px; height: 260px; background: #e9d5ff; animation: pmk-float 6s ease-in-out infinite; }
        .pmk-orb-2 { top: 25%; right: -80px; width: 220px; height: 220px; background: #c7d2fe; animation: pmk-float 7s ease-in-out infinite 1s; }
        .pmk-orb-3 { bottom: 40px; left: 15%; width: 180px; height: 180px; background: #ddd6fe; animation: pmk-float 8s ease-in-out infinite 2s; }
        .pmk-orb-4 { bottom: 0; right: 20%; width: 140px; height: 140px; background: #d8b4fe; animation: pmk-float 9s ease-in-out infinite 3s; }
        @keyframes pmk-float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-14px); } }

        .pmk-card {
            position: relative;
            width: 100%;
            max-width: 30rem;
            padding: 2.25rem;
            border-radius: 1.25rem;
            background: rgba(255, 255, 255, 0.92);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.6);
            box-shadow: 0 20px 60px -20px rgba(76, 29, 149, 0.25);
            text-align: center;
            animation: pmk-fade 0.5s ease-out forwards;
        }
        @keyframes pmk-fade { from { opacity: 0; transform: translateY(12px); } to { opacity: 1; transform: translateY(0); } }

        .pmk-icon-wrap {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 5rem;
            height: 5rem;
            border-radius: 9999px;
            background: linear-gradient(135deg, #f3e8ff, #e0e7ff);
            margin-bottom: 1.5rem;
            animation: pmk-pop 0.55s cubic-bezier(.34,1.56,.64,1) forwards;
        }
        @keyframes pmk-pop { 0% { transform: scale(0.6); opacity: 0; } 60% { transform: scale(1.15); opacity: 1; } 100% { transform: scale(1); opacity: 1; } }
        .pmk-check-path { stroke-dasharray: 48; stroke-dashoffset: 48; animation: pmk-check 0.5s ease-out 0.3s forwards; }
        @keyframes pmk-check { to { stroke-dashoffset: 0; } }

        .pmk-title { font-size: 1.5rem; font-weight: 700; color: #1f2937; margin: 0 0 0.5rem 0; letter-spacing: -0.01em; }
        .pmk-sub   { color: #6b7280; font-size: 0.9375rem; margin: 0 0 1.5rem 0; line-height: 1.55; }
        .pmk-hint  { color: #6b7280; font-size: 0.875rem; margin: 0 0 1rem 0; }

        .pmk-creds-box {
            text-align: left;
            padding: 1rem 1.125rem;
            border-radius: 0.875rem;
            background: linear-gradient(135deg, rgba(124, 58, 237, 0.06), rgba(99, 102, 241, 0.05));
            border: 1px solid rgba(124, 58, 237, 0.15);
            margin: 0 0 1.5rem 0;
        }
        .pmk-creds-heading {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-weight: 600;
            font-size: 0.8125rem;
            color: #7c3aed;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 0.75rem;
        }
        .pmk-creds-heading .material-symbols-outlined { font-size: 1rem; }
        .pmk-creds-row {
            display: flex;
            align-items: flex-start;
            gap: 0.625rem;
            padding: 0.375rem 0;
            color: #374151;
            font-size: 0.875rem;
            line-height: 1.5;
        }
        .pmk-creds-row .material-symbols-outlined { font-size: 1.125rem; color: #7c3aed; margin-top: 0.0625rem; }
        .pmk-creds-row strong { color: #111827; font-weight: 600; }

        .pmk-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            width: 100%;
            padding: 0.9375rem 1rem;
            border-radius: 0.75rem;
            color: white;
            font-weight: 600;
            font-size: 1rem;
            text-decoration: none;
            border: none;
            cursor: pointer;
            background: linear-gradient(-45deg, #7c3aed, #a855f7, #7c3aed);
            background-size: 400% 400%;
            animation: pmk-gradient 6s ease infinite;
            transition: transform 0.15s ease, box-shadow 0.15s ease;
        }
        .pmk-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 20px -6px rgba(124, 58, 237, 0.5); }
        .pmk-btn:active { transform: translateY(0); }
        @keyframes pmk-gradient { 0%, 100% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } }

        .pmk-footer {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.4rem;
            margin-top: 1.75rem;
            color: #9ca3af;
            font-size: 0.75rem;
        }

        .pmk-actions-list {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
            margin: 0 0 1.5rem 0;
            padding: 0.875rem;
            background: rgba(124, 58, 237, 0.06);
            border-radius: 0.75rem;
            border: 1px solid rgba(124, 58, 237, 0.12);
        }
        .pmk-action-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 0.8125rem;
            color: #4b5563;
        }
        .pmk-action-item .material-symbols-outlined { font-size: 1rem; color: #7c3aed; }

        @media (max-width: 480px) {
            .pmk-card { padding: 1.75rem 1.5rem; }
            .pmk-title { font-size: 1.25rem; }
        }
    </style>
<#elseif section = "form">

<#-- Detect the "password/account updated" completion state.
     Keycloak sets pageRedirectUri (client's URL) and has no pending requiredActions
     when the required-actions flow has finished successfully. -->
<#assign hasPendingActions = requiredActions?? && requiredActions?is_sequence && requiredActions?size gt 0 />
<#assign isCompletion = pageRedirectUri?has_content && !hasPendingActions />

<div class="pmk-info-shell">
    <div class="pmk-orb pmk-orb-1"></div>
    <div class="pmk-orb pmk-orb-2"></div>
    <div class="pmk-orb pmk-orb-3"></div>
    <div class="pmk-orb pmk-orb-4"></div>

    <div class="pmk-card">
        <div class="pmk-icon-wrap">
            <svg xmlns="http://www.w3.org/2000/svg" width="44" height="44" viewBox="0 0 24 24" fill="none"
                 stroke="#7c3aed" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <path class="pmk-check-path" d="M4 12.5l5 5L20 6"/>
            </svg>
        </div>

        <#if isCompletion>
            <h1 class="pmk-title">You're all set!</h1>
            <p class="pmk-sub">Your password has been saved. You can now log in to Pinepetosan Marketplace.</p>

            <div class="pmk-creds-box">
                <div class="pmk-creds-heading">
                    <span class="material-symbols-outlined">key</span>
                    Your login credentials
                </div>
                <div class="pmk-creds-row">
                    <span class="material-symbols-outlined">mail</span>
                    <span><strong>Email:</strong> the email address your invitation was sent to</span>
                </div>
                <div class="pmk-creds-row">
                    <span class="material-symbols-outlined">lock</span>
                    <span><strong>Password:</strong> the password you just created</span>
                </div>
            </div>

            <a href="${pageRedirectUri}" class="pmk-btn">
                <span class="material-symbols-outlined">login</span>
                Log in to your account
            </a>
        <#else>
            <h1 class="pmk-title">
                <#if messageHeader??>${messageHeader}<#else>${message.summary?no_esc}</#if>
            </h1>

            <#if messageHeader?? && message?? && message.summary??>
                <p class="pmk-sub">${message.summary?no_esc}</p>
            </#if>

            <#if hasPendingActions>
                <div class="pmk-actions-list">
                    <#list requiredActions as reqActionItem>
                        <div class="pmk-action-item">
                            <span class="material-symbols-outlined">
                                <#if reqActionItem == "UPDATE_PASSWORD">lock_reset<#elseif reqActionItem == "VERIFY_EMAIL">mark_email_read<#elseif reqActionItem == "UPDATE_PROFILE">person<#elseif reqActionItem == "CONFIGURE_TOTP">phonelink_lock<#else>task_alt</#if>
                            </span>
                            <span>${msg("requiredAction.${reqActionItem}")}</span>
                        </div>
                    </#list>
                </div>
            </#if>

            <#if actionUri?has_content>
                <a href="${actionUri}" class="pmk-btn">
                    <span class="material-symbols-outlined">arrow_forward</span>
                    Continue
                </a>
            <#elseif pageRedirectUri?has_content>
                <a href="${pageRedirectUri}" class="pmk-btn">
                    <span class="material-symbols-outlined">arrow_forward</span>
                    Continue
                </a>
            <#elseif (client.baseUrl)?has_content>
                <a href="${client.baseUrl}" class="pmk-btn">
                    <span class="material-symbols-outlined">storefront</span>
                    Back to Pinepetosan Marketplace
                </a>
            </#if>
        </#if>

        <div class="pmk-footer">
            <span class="material-symbols-outlined" style="font-size: 0.875rem;">shield_lock</span>
            <span>Pinepetosan Marketplace &middot; Secure authentication</span>
        </div>
    </div>
</div>

</#if>
</@layout.registrationLayout>
