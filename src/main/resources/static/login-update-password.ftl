<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('password','password-confirm'); section>
<#if section = "header">
    <title>Pinepetosan Marketplace - Set New Password</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="preconnect" href="https://fonts.gstatic.com/" crossorigin />
    <link rel="stylesheet" as="style" onload="this.rel='stylesheet'"
          href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap"/>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <style>
        .login-pf-page-header,
        .login-pf-header,
        h1#kc-page-title,
        .login-pf-signup { display: none !important; }
        .login-pf-page { padding-top: 0; border: none; }
        .login-pf-page .card-pf { padding: 0; margin-bottom: 0; border: none; max-width: none; }
        #kc-content-wrapper { margin-top: 0; }
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .comtext { color: white !important; }

        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
        @keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
        @keyframes slideDown { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
        @keyframes float { 0% { transform: translateY(0px); } 50% { transform: translateY(-5px); } 100% { transform: translateY(0px); } }
        @keyframes gradientShift { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }
        @keyframes pulse { 0% { transform: scale(1); } 50% { transform: scale(1.05); } 100% { transform: scale(1); } }

        .animate-fade-in { animation: fadeIn 0.6s ease-out forwards; }
        .animate-pulse-slow { animation: pulse 3s infinite ease-in-out; }
        .animate-float { animation: float 4s infinite ease-in-out; }
        .animate-gradient {
            background: linear-gradient(-45deg, #630ed4, #7c3aed, #630ed4);
            background-size: 400% 400%;
            animation: gradientShift 8s ease infinite;
        }
        .mobile-fade-in { animation: fadeInUp 0.5s ease-out forwards; }
        .mobile-slide-down { animation: slideDown 0.3s ease-out forwards; }

        .btn-transition { transition: all 0.2s ease; }
        .btn-transition:hover { transform: translateY(-2px); box-shadow: 0 4px 6px -1px rgba(0,0,0,.1), 0 2px 4px -1px rgba(0,0,0,.06); }
        .mobile-btn-active:active { transform: scale(0.98); }

        .gradient-text {
            background: linear-gradient(90deg, #630ed4, #7c3aed);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        .glass-effect {
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.18);
        }
        .mobile-glass-effect {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
        }
        .input-focus:focus { box-shadow: 0 0 0 3px rgba(99, 14, 212, 0.2); }
        .mobile-input-focus:focus { box-shadow: 0 0 0 3px rgba(99, 14, 212, 0.2); border-color: #630ed4; }

        .strength-bar { height: 4px; border-radius: 9999px; transition: all 0.3s ease; background: #e5e7eb; }
        .strength-weak   { background: #ef4444; }
        .strength-fair   { background: #f59e0b; }
        .strength-good   { background: #3b82f6; }
        .strength-strong { background: #10b981; }

        .field-error { border-color: #ef4444 !important; box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.15) !important; }
        .field-ok    { border-color: #10b981 !important; box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15) !important; }
        .pmk-match-hint { display: none; align-items: center; gap: 0.35rem; font-size: 0.8125rem; margin-top: 0.5rem; }
        .pmk-match-hint.show { display: inline-flex; }
        .pmk-match-hint.err { color: #dc2626; }
        .pmk-match-hint.ok  { color: #059669; }
        .pmk-btn-disabled { opacity: 0.55; pointer-events: none; filter: grayscale(0.3); }

        .mobile-touch-target { min-height: 48px; min-width: 48px; }

        @media (max-width: 1023px) {
            input[type="password"], input[type="text"] { font-size: 16px; }
        }

        .desktop-layout { display: none; }
        .mobile-layout { display: block; }
        @media (min-width: 1024px) {
            .desktop-layout { display: flex !important; }
            .mobile-layout { display: none !important; }
            .desktop-left-panel { display: flex !important; flex-direction: column; justify-content: space-between; }
            .responsive-right-panel { width: 50% !important; }
            .desktop-background { display: block; }
            .mobile-background { display: none; }
        }
        @media (max-width: 1023px) {
            .desktop-layout { display: none !important; }
            .mobile-layout { display: block !important; }
            .desktop-background { display: none; }
            .mobile-background { display: block; }
            .mobile-container { min-height: 100vh; display: flex; flex-direction: column; }
            .mobile-header { flex-shrink: 0; padding: 3rem 1.5rem 2rem; }
            .mobile-content { flex: 1; padding: 0 1.5rem 2rem; }
            .mobile-form-container { max-width: 20rem; margin: 0 auto; }
            .mobile-form-card { border-radius: 1.5rem; padding: 2rem; }
            .mobile-input { padding: 0.75rem; border-radius: 0.5rem; border-width: 2px; font-size: 1rem; }
            .mobile-input-with-icon { padding-left: 2.5rem; }
            .mobile-button { padding: 0.875rem 1.25rem; border-radius: 0.75rem; font-size: 1rem; font-weight: 700; }
            .mobile-icon-container { padding-left: 0.75rem; }
            .mobile-icon { font-size: 1rem; }
        }
    </style>
<#elseif section = "form">
<body class="bg-gradient-to-br from-purple-50 to-indigo-50 min-h-screen"
      style="font-family: 'Plus Jakarta Sans', sans-serif">

<div class="desktop-background fixed top-0 left-0 w-full h-full overflow-hidden z-0">
    <div class="absolute -top-20 -left-20 w-72 h-72 bg-purple-200 rounded-full opacity-20 animate-float" style="animation-delay: 0s"></div>
    <div class="absolute top-1/4 -right-20 w-64 h-64 bg-indigo-200 rounded-full opacity-20 animate-float" style="animation-delay: 1s"></div>
    <div class="absolute bottom-20 left-1/4 w-56 h-56 bg-violet-200 rounded-full opacity-20 animate-float" style="animation-delay: 2s"></div>
    <div class="absolute bottom-0 right-1/4 w-48 h-48 bg-purple-300 rounded-full opacity-20 animate-float" style="animation-delay: 3s"></div>
</div>

<div class="mobile-background fixed inset-0 overflow-hidden z-0">
    <div class="absolute -top-10 -left-10 w-40 h-40 bg-purple-200 rounded-full opacity-20"></div>
    <div class="absolute top-1/4 -right-10 w-32 h-32 bg-indigo-200 rounded-full opacity-20"></div>
    <div class="absolute bottom-20 left-1/4 w-28 h-28 bg-violet-200 rounded-full opacity-20"></div>
</div>

<!-- Desktop layout -->
<div class="desktop-layout min-h-screen relative z-10">
    <div class="desktop-left-panel w-1/2 p-12 bg-gradient-to-br from-purple-600 to-indigo-600 text-white">
        <div class="flex items-center gap-3 mb-12 animate-fade-in">
            <div class="w-10 h-10 animate-pulse-slow">
                <span class="material-symbols-outlined text-4xl text-white">shopping_bag</span>
            </div>
            <h1 class="text-2xl font-bold">Pinepetosan Marketplace</h1>
        </div>
        <div class="max-w-md animate-fade-in" style="animation-delay: 0.3s">
            <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-white bg-opacity-20 mb-6">
                <span class="material-symbols-outlined text-5xl text-white">key</span>
            </div>
            <h2 class="text-4xl font-bold mb-6">Secure your account</h2>
            <p class="text-lg opacity-90 mb-8 comtext">Choose a strong password to protect your Pinepetosan Marketplace account. You'll be signed in automatically once it's set.</p>
            <div class="space-y-4">
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">shield_lock</span>
                    </div>
                    <p class="text-lg comtext">Use at least 8 characters</p>
                </div>
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">password</span>
                    </div>
                    <p class="text-lg comtext">Mix letters, numbers, symbols</p>
                </div>
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">lock_reset</span>
                    </div>
                    <p class="text-lg comtext">Avoid reusing old passwords</p>
                </div>
            </div>
        </div>
        <p class="text-sm opacity-80 comtext animate-fade-in" style="animation-delay: 0.6s">
            Your credentials are encrypted end-to-end. We never see or store your password in plain text.
        </p>
    </div>

    <div class="responsive-right-panel w-1/2 flex items-center justify-center p-8">
        <div class="w-full max-w-md">
            <div class="glass-effect rounded-2xl shadow-xl p-8 animate-fade-in" style="animation-delay: 0.2s">
                <h2 class="text-2xl font-bold text-gray-800 mb-2">Set a new password</h2>
                <p class="text-gray-600 mb-6 text-sm">Enter your new password below. You'll be signed in immediately after.</p>

                <form id="kc-passwd-update-form-desktop" class="space-y-5" action="${url.loginAction}" method="post">
                    <input readonly value="this is not a login form" style="display:none;" type="text" name="username" />
                    <input readonly value="this is not a login form" style="display:none;" type="password" name="password" autocomplete="off" />

                    <div>
                        <label for="password-new-desktop" class="block text-sm font-medium text-gray-700 mb-1">${msg("passwordNew")}</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400">lock</span>
                            </div>
                            <input type="password" id="password-new-desktop" name="password-new"
                                   class="input-focus pl-10 pr-10 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full transition-all duration-200"
                                   placeholder="••••••••"
                                   autofocus autocomplete="new-password"
                                   aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"/>
                            <button type="button" class="toggle-pwd absolute inset-y-0 right-0 pr-3 flex items-center" data-target="password-new-desktop">
                                <span class="material-symbols-outlined text-gray-400 hover:text-gray-600">visibility</span>
                            </button>
                        </div>
                        <div class="mt-2 grid grid-cols-4 gap-1">
                            <div class="strength-bar" data-strength="1"></div>
                            <div class="strength-bar" data-strength="2"></div>
                            <div class="strength-bar" data-strength="3"></div>
                            <div class="strength-bar" data-strength="4"></div>
                        </div>
                        <p class="text-xs text-gray-500 mt-1" id="strength-label-desktop">Password strength</p>
                        <#if messagesPerField.existsError('password')>
                            <p class="text-red-600 text-sm mt-1">${kcSanitize(messagesPerField.getFirstError('password'))?no_esc}</p>
                        </#if>
                    </div>

                    <div>
                        <label for="password-confirm-desktop" class="block text-sm font-medium text-gray-700 mb-1">${msg("passwordConfirm")}</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400">lock</span>
                            </div>
                            <input type="password" id="password-confirm-desktop" name="password-confirm"
                                   class="input-focus pl-10 pr-10 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full transition-all duration-200"
                                   placeholder="••••••••"
                                   autocomplete="new-password"
                                   aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"/>
                            <button type="button" class="toggle-pwd absolute inset-y-0 right-0 pr-3 flex items-center" data-target="password-confirm-desktop">
                                <span class="material-symbols-outlined text-gray-400 hover:text-gray-600">visibility</span>
                            </button>
                        </div>
                        <div id="match-hint-desktop" class="pmk-match-hint">
                            <span class="material-symbols-outlined" style="font-size: 1rem;"></span>
                            <span class="match-text"></span>
                        </div>
                        <#if messagesPerField.existsError('password-confirm')>
                            <p class="text-red-600 text-sm mt-1">${kcSanitize(messagesPerField.getFirstError('password-confirm'))?no_esc}</p>
                        </#if>
                    </div>

                    <#if isAppInitiatedAction??>
                        <div class="flex items-center">
                            <input type="checkbox" id="logout-sessions-desktop" name="logout-sessions" value="on" checked
                                   class="h-4 w-4 text-purple-600 focus:ring-purple-500 border-gray-300 rounded"/>
                            <label for="logout-sessions-desktop" class="ml-2 block text-sm text-gray-700">${msg("logoutOtherSessions")}</label>
                        </div>
                    </#if>

                    <div>
                        <button type="submit" id="kc-submit-desktop"
                                class="animate-gradient w-full py-3 px-4 rounded-lg text-white font-medium btn-transition focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500">
                            ${msg("doSubmit")}
                        </button>
                    </div>

                    <#if isAppInitiatedAction??>
                        <div class="text-center">
                            <button type="submit" name="cancel-aia" value="true"
                                    class="text-sm text-gray-600 hover:text-purple-600 transition-colors">
                                ${msg("doCancel")}
                            </button>
                        </div>
                    </#if>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Mobile layout -->
<div class="mobile-layout mobile-container relative z-10">
    <div class="mobile-header">
        <div class="text-center mobile-fade-in">
            <div class="inline-flex items-center justify-center mb-4">
                <div class="w-16 h-16">
                    <span class="material-symbols-outlined text-6xl text-purple-600">key</span>
                </div>
            </div>
            <h1 class="text-3xl font-bold gradient-text mb-2">Set new password</h1>
            <p class="text-gray-600 text-base">You'll be signed in automatically</p>
        </div>
    </div>

    <div class="mobile-content">
        <div class="mobile-form-container">
            <div class="mobile-glass-effect mobile-form-card mobile-fade-in" style="animation-delay: 0.2s">
                <form id="kc-passwd-update-form-mobile" action="${url.loginAction}" method="post" class="space-y-6">
                    <input readonly value="this is not a login form" style="display:none;" type="text" name="username" />
                    <input readonly value="this is not a login form" style="display:none;" type="password" name="password" autocomplete="off" />

                    <div class="mobile-slide-down" style="animation-delay: 0.3s">
                        <label for="password-new-mobile" class="block text-sm font-semibold text-gray-700 mb-3">${msg("passwordNew")}</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">lock</span>
                            </div>
                            <input type="password" id="password-new-mobile" name="password-new"
                                   class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon pr-14 border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="Enter new password"
                                   autocomplete="new-password"
                                   aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"/>
                            <button type="button" class="toggle-pwd absolute inset-y-0 right-0 pr-4 flex items-center mobile-touch-target" data-target="password-new-mobile">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">visibility</span>
                            </button>
                        </div>
                        <div class="mt-3 grid grid-cols-4 gap-1">
                            <div class="strength-bar" data-strength="1"></div>
                            <div class="strength-bar" data-strength="2"></div>
                            <div class="strength-bar" data-strength="3"></div>
                            <div class="strength-bar" data-strength="4"></div>
                        </div>
                        <p class="text-xs text-gray-500 mt-2" id="strength-label-mobile">Password strength</p>
                        <#if messagesPerField.existsError('password')>
                            <p class="text-red-600 text-sm mt-2 font-medium">${kcSanitize(messagesPerField.getFirstError('password'))?no_esc}</p>
                        </#if>
                    </div>

                    <div class="mobile-slide-down" style="animation-delay: 0.4s">
                        <label for="password-confirm-mobile" class="block text-sm font-semibold text-gray-700 mb-3">${msg("passwordConfirm")}</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">lock</span>
                            </div>
                            <input type="password" id="password-confirm-mobile" name="password-confirm"
                                   class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon pr-14 border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="Confirm new password"
                                   autocomplete="new-password"
                                   aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"/>
                            <button type="button" class="toggle-pwd absolute inset-y-0 right-0 pr-4 flex items-center mobile-touch-target" data-target="password-confirm-mobile">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">visibility</span>
                            </button>
                        </div>
                        <div id="match-hint-mobile" class="pmk-match-hint">
                            <span class="material-symbols-outlined" style="font-size: 1rem;"></span>
                            <span class="match-text"></span>
                        </div>
                        <#if messagesPerField.existsError('password-confirm')>
                            <p class="text-red-600 text-sm mt-2 font-medium">${kcSanitize(messagesPerField.getFirstError('password-confirm'))?no_esc}</p>
                        </#if>
                    </div>

                    <#if isAppInitiatedAction??>
                        <div class="flex items-center mobile-slide-down" style="animation-delay: 0.5s">
                            <input type="checkbox" id="logout-sessions-mobile" name="logout-sessions" value="on" checked
                                   class="h-5 w-5 text-purple-600 focus:ring-purple-500 border-gray-300 rounded"/>
                            <label for="logout-sessions-mobile" class="ml-3 block text-sm text-gray-700 font-medium">${msg("logoutOtherSessions")}</label>
                        </div>
                    </#if>

                    <div class="mobile-slide-down" style="animation-delay: 0.6s">
                        <button type="submit" id="kc-submit-mobile"
                                class="animate-gradient mobile-btn-active mobile-touch-target mobile-button w-full text-white focus:outline-none focus:ring-4 focus:ring-purple-300 transition-all duration-200">
                            ${msg("doSubmit")}
                        </button>
                    </div>

                    <#if isAppInitiatedAction??>
                        <div class="text-center mobile-slide-down" style="animation-delay: 0.7s">
                            <button type="submit" name="cancel-aia" value="true"
                                    class="text-sm text-gray-600 font-medium">${msg("doCancel")}</button>
                        </div>
                    </#if>
                </form>
            </div>

            <div class="mt-8 text-center mobile-slide-down" style="animation-delay: 0.8s">
                <div class="flex justify-center items-center space-x-4">
                    <div class="flex items-center space-x-1">
                        <span class="material-symbols-outlined text-green-500 text-sm">shield_lock</span>
                        <span class="text-xs text-gray-600 font-medium">Encrypted</span>
                    </div>
                    <div class="flex items-center space-x-1">
                        <span class="material-symbols-outlined text-purple-500 text-sm">bolt</span>
                        <span class="text-xs text-gray-600 font-medium">Auto sign-in</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.querySelectorAll(".toggle-pwd").forEach(function (btn) {
        btn.addEventListener("click", function () {
            var input = document.getElementById(btn.getAttribute("data-target"));
            var icon = btn.querySelector("span");
            if (!input) return;
            if (input.type === "password") { input.type = "text"; icon.textContent = "visibility_off"; }
            else { input.type = "password"; icon.textContent = "visibility"; }
        });
    });

    function scorePassword(pwd) {
        if (!pwd) return 0;
        var score = 0;
        if (pwd.length >= 8) score++;
        if (pwd.length >= 12) score++;
        if (/[A-Z]/.test(pwd) && /[a-z]/.test(pwd)) score++;
        if (/[0-9]/.test(pwd) && /[^A-Za-z0-9]/.test(pwd)) score++;
        return Math.min(score, 4);
    }
    function paintStrength(scope, score) {
        var bars = scope.querySelectorAll(".strength-bar");
        var labels = ["Password strength", "Weak", "Fair", "Good", "Strong"];
        var classes = ["", "strength-weak", "strength-fair", "strength-good", "strength-strong"];
        bars.forEach(function (bar, i) {
            bar.classList.remove("strength-weak","strength-fair","strength-good","strength-strong");
            if (i < score) bar.classList.add(classes[score]);
        });
        var lbl = scope.querySelector("[id^='strength-label-']");
        if (lbl) lbl.textContent = labels[score];
    }
    function checkMatch(variant) {
        var pwd     = document.getElementById("password-new-" + variant);
        var confirm = document.getElementById("password-confirm-" + variant);
        var hint    = document.getElementById("match-hint-" + variant);
        var submit  = document.getElementById("kc-submit-" + variant);
        if (!pwd || !confirm || !hint || !submit) return;
        var icon = hint.querySelector(".material-symbols-outlined");
        var text = hint.querySelector(".match-text");

        confirm.classList.remove("field-error", "field-ok");
        hint.classList.remove("show", "err", "ok");
        submit.classList.remove("pmk-btn-disabled");
        submit.removeAttribute("aria-disabled");

        if (confirm.value.length === 0) return;

        if (pwd.value === confirm.value) {
            confirm.classList.add("field-ok");
            hint.classList.add("show", "ok");
            icon.textContent = "check_circle";
            text.textContent = "Passwords match";
        } else {
            confirm.classList.add("field-error");
            hint.classList.add("show", "err");
            icon.textContent = "error";
            text.textContent = "Passwords don't match";
            submit.classList.add("pmk-btn-disabled");
            submit.setAttribute("aria-disabled", "true");
        }
    }

    ["desktop", "mobile"].forEach(function (variant) {
        var input   = document.getElementById("password-new-" + variant);
        var confirm = document.getElementById("password-confirm-" + variant);
        var form    = document.getElementById("kc-passwd-update-form-" + variant);
        if (input && form) {
            input.addEventListener("input", function () {
                paintStrength(form, scorePassword(input.value));
                checkMatch(variant);
            });
        }
        if (confirm) {
            confirm.addEventListener("input", function () { checkMatch(variant); });
            confirm.addEventListener("blur",  function () { checkMatch(variant); });
        }
        var submitBtn = document.getElementById("kc-submit-" + variant);
        if (form && submitBtn) {
            form.addEventListener("submit", function (e) {
                if (e.submitter && e.submitter.name === "cancel-aia") return;
                if (input && confirm && input.value !== confirm.value) {
                    e.preventDefault();
                    checkMatch(variant);
                    confirm.focus();
                    return;
                }
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="material-symbols-outlined animate-spin mr-2">progress_activity</span>Setting password...';
            });
        }
    });
</script>

</#if>
</@layout.registrationLayout>
