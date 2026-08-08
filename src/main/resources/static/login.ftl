<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
<#if section = "header">
    <title>Pinepetosan Marketplace - Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="preconnect" href="https://fonts.gstatic.com/" crossorigin />
    <link rel="stylesheet" as="style" onload="this.rel='stylesheet'"
          href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap"/>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <style>
        /* Hide default Keycloak elements */
        .login-pf-page-header,
        .login-pf-header,
        h1#kc-page-title,
        .login-pf-signup {
            display: none !important;
        }

        .login-pf-page {
            padding-top: 0;
            border: none;
        }

        .login-pf-page .card-pf {
            padding: 0;
            margin-bottom: 0;
            border: none;
            max-width: none;
        }

        #kc-content-wrapper {
            margin-top: 0;
        }

        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }

        .comtext {
            color: white !important;
        }

        /* Base animations */
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @keyframes slideIn {
            from {
                transform: translateX(-20px);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
        }

        @keyframes float {
            0% { transform: translateY(0px); }
            50% { transform: translateY(-5px); }
            100% { transform: translateY(0px); }
        }

        @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            75% { transform: translateX(5px); }
        }

        /* Animation classes */
        .animate-fade-in {
            animation: fadeIn 0.6s ease-out forwards;
        }

        .animate-slide-in {
            animation: slideIn 0.5s ease-out forwards;
        }

        .animate-pulse-slow {
            animation: pulse 3s infinite ease-in-out;
        }

        .animate-float {
            animation: float 4s infinite ease-in-out;
        }

        .animate-gradient {
            background: linear-gradient(-45deg, #630ed4, #7c3aed, #630ed4);
            background-size: 400% 400%;
            animation: gradientShift 8s ease infinite;
        }

        .animate-shake {
            animation: shake 0.3s ease-in-out;
        }

        /* Mobile-specific animations */
        .mobile-fade-in {
            animation: fadeInUp 0.5s ease-out forwards;
        }

        .mobile-slide-down {
            animation: slideDown 0.3s ease-out forwards;
        }

        /* Button transitions */
        .btn-transition {
            transition: all 0.2s ease;
        }

        .btn-transition:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        }

        /* Mobile button active state */
        .mobile-btn-active:active {
            transform: scale(0.98);
        }

        /* Text effects */
        .gradient-text {
            background: linear-gradient(90deg, #630ed4, #7c3aed);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        /* Glass effects */
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

        /* Input focus states */
        .input-focus:focus {
            box-shadow: 0 0 0 3px rgba(99, 14, 212, 0.2);
        }

        .mobile-input-focus:focus {
            box-shadow: 0 0 0 3px rgba(99, 14, 212, 0.2);
            border-color: #630ed4;
        }

        /* Checkbox styling */
        .checkbox:checked {
            background-image: url("data:image/svg+xml,%3csvg viewBox='0 0 16 16' fill='white' xmlns='http://www.w3.org/2000/svg'%3e%3cpath d='M12.207 4.793a1 1 0 010 1.414l-5 5a1 1 0 01-1.414 0l-2-2a1 1 0 011.414-1.414L6.5 9.086l4.293-4.293a1 1 0 011.414 0z'/%3e%3c/svg%3e");
            background-color: #630ed4;
            border-color: #630ed4;
        }

        /* Mobile touch targets */
        .mobile-touch-target {
            min-height: 48px;
            min-width: 48px;
        }

        /* Prevent zoom on input focus for mobile */
        @media (max-width: 1023px) {
            input[type="text"],
            input[type="email"],
            input[type="password"] {
                font-size: 16px;
            }
        }

        /* Desktop layout (1024px and up) */
        .desktop-layout {
            display: none;
        }

        .mobile-layout {
            display: block;
        }

        @media (min-width: 1024px) {
            .desktop-layout {
                display: flex !important;
            }

            .mobile-layout {
                display: none !important;
            }

            .desktop-left-panel {
                display: flex !important;
                flex-direction: column;
                justify-content: space-between;
            }

            .responsive-right-panel {
                width: 50% !important;
            }

            /* Desktop-specific background */
            .desktop-background {
                display: block;
            }
            .mobile-background {
                display: none;
            }
        }

        /* Mobile layout (below 1024px) */
        @media (max-width: 1023px) {
            .desktop-layout {
                display: none !important;
            }

            .mobile-layout {
                display: block !important;
            }

            /* Mobile-specific background */
            .desktop-background {
                display: none;
            }
            .mobile-background {
                display: block;
            }

            /* Mobile-specific layout adjustments */
            .mobile-container {
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }

            .mobile-header {
                flex-shrink: 0;
                padding-top: 3rem;
                padding-bottom: 2rem;
                padding-left: 1.5rem;
                padding-right: 1.5rem;
            }

            .mobile-content {
                flex: 1;
                padding-left: 1.5rem;
                padding-right: 1.5rem;
                padding-bottom: 2rem;
            }

            .mobile-form-container {
                max-width: 20rem;
                margin: 0 auto;
            }

            /* Mobile form styling */
            .mobile-form-card {
                border-radius: 1.5rem;
                padding: 2rem;
            }

            .mobile-input {
                padding: 0.75rem;
                border-radius: 0.5rem;
                border-width: 2px;
                font-size: 1rem;
            }

            .mobile-input-with-icon {
                padding-left: 2.5rem;
            }

            .mobile-button {
                padding: 0.875rem 1.25rem;
                border-radius: 0.75rem;
                font-size: 1rem;
                font-weight: 700;
            }

            .mobile-icon-container {
                padding-left: 0.75rem;
            }

            .mobile-icon {
                font-size: 1rem;
            }
        }

    </style>
<#elseif section = "form">

<body class="bg-gradient-to-br from-purple-50 to-indigo-50 min-h-screen"
      style="font-family: 'Plus Jakarta Sans', sans-serif">

<!-- Desktop background -->
<div class="desktop-background fixed top-0 left-0 w-full h-full overflow-hidden z-0">
    <div class="absolute -top-20 -left-20 w-72 h-72 bg-purple-200 rounded-full opacity-20 animate-float" style="animation-delay: 0s"></div>
    <div class="absolute top-1/4 -right-20 w-64 h-64 bg-indigo-200 rounded-full opacity-20 animate-float" style="animation-delay: 1s"></div>
    <div class="absolute bottom-20 left-1/4 w-56 h-56 bg-violet-200 rounded-full opacity-20 animate-float" style="animation-delay: 2s"></div>
    <div class="absolute bottom-0 right-1/4 w-48 h-48 bg-purple-300 rounded-full opacity-20 animate-float" style="animation-delay: 3s"></div>
</div>

<!-- Mobile background -->
<div class="mobile-background fixed inset-0 overflow-hidden z-0">
    <div class="absolute -top-10 -left-10 w-40 h-40 bg-purple-200 rounded-full opacity-20"></div>
    <div class="absolute top-1/4 -right-10 w-32 h-32 bg-indigo-200 rounded-full opacity-20"></div>
    <div class="absolute bottom-20 left-1/4 w-28 h-28 bg-violet-200 rounded-full opacity-20"></div>
</div>

<!-- Desktop layout -->
<div class="desktop-layout min-h-screen relative z-10">
    <!-- Left branding panel (desktop only) -->
    <div class="desktop-left-panel w-1/2 p-12 bg-gradient-to-br from-purple-600 to-indigo-600 text-white">
        <div class="flex items-center gap-3 mb-12 animate-fade-in">
            <div class="w-10 h-10 animate-pulse-slow">
                <span class="material-symbols-outlined text-4xl text-white">shopping_bag</span>
            </div>
            <h1 class="text-2xl font-bold">Pinepetosan Marketplace</h1>
        </div>
        <div class="max-w-md animate-fade-in" style="animation-delay: 0.3s">
            <h2 class="text-4xl font-bold mb-6">Shop Everything. Sell Anything.</h2>
            <p class="text-lg opacity-90 mb-8 comtext">Nigeria's most trusted marketplace for verified vendors and fast nationwide delivery. Experience seamless shopping from Lagos to Kano.</p>
            <div class="space-y-6">
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">verified</span>
                    </div>
                    <p class="text-lg comtext">Verified vendors nationwide</p>
                </div>
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">local_shipping</span>
                    </div>
                    <p class="text-lg comtext">Fast delivery across Nigeria</p>
                </div>
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">security</span>
                    </div>
                    <p class="text-lg comtext">Secure payment protection</p>
                </div>
            </div>
        </div>
        <div class="animate-fade-in" style="animation-delay: 0.6s">
            <div class="flex items-center space-x-2 mb-4">
                <div class="w-8 h-8 rounded-full border-2 border-white overflow-hidden">
                    <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&amp;ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&amp;auto=format&amp;fit=crop&amp;w=100&amp;q=80" alt="User" class="w-full h-full object-cover">
                </div>
                <div class="w-8 h-8 rounded-full border-2 border-white overflow-hidden">
                    <img src="https://images.unsplash.com/photo-1544005313-94ddf0286df2?ixlib=rb-4.0.3&amp;ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&amp;auto=format&amp;fit=crop&amp;w=100&amp;q=80" alt="User" class="w-full h-full object-cover">
                </div>
                <div class="w-8 h-8 rounded-full border-2 border-white overflow-hidden">
                    <img src="https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?ixlib=rb-4.0.3&amp;ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&amp;auto=format&amp;fit=crop&amp;w=100&amp;q=80" alt="User" class="w-full h-full object-cover">
                </div>
                <div class="w-8 h-8 rounded-full border-2 border-white overflow-hidden">
                    <img src="https://images.unsplash.com/photo-1519345182560-3f2917c472ef?ixlib=rb-4.0.3&amp;ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&amp;auto=format&amp;fit=crop&amp;w=100&amp;q=80" alt="User" class="w-full h-full object-cover">
                </div>
            </div>
            <p class="text-sm opacity-80 comtext">
                Join thousands of Nigerians who trust Pinepetosan Marketplace for their shopping needs
            </p>
        </div>
    </div>

    <!-- Desktop login form -->
    <div class="responsive-right-panel w-1/2 flex items-center justify-center p-8">
        <div class="w-full max-w-md">
            <div class="glass-effect rounded-2xl shadow-xl p-8 animate-fade-in" style="animation-delay: 0.2s">
                <h2 class="text-2xl font-bold text-gray-800 mb-2">Welcome back</h2>
                <p class="text-gray-600 mb-6 text-sm">Sign in to your Pinepetosan Marketplace account</p>

                <form id="kc-form-login-desktop" class="space-y-5" action="${url.loginAction}" method="post">
                    <!-- Username/Email -->
                    <div>
                        <label for="username-desktop" class="block text-sm font-medium text-gray-700 mb-1">
                            <#if !realm.loginWithEmailAllowed>${msg("username")}
                            <#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}
                            <#else>${msg("email")}</#if>
                        </label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400">mail</span>
                            </div>
                            <input type="text" id="username-desktop" name="username"
                                   value="${(login.username!'')}"
                                   class="input-focus pl-10 pr-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full transition-all duration-200"
                                   autocomplete="username"
                                   placeholder="you@example.com"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
                        </div>
                        <#if messagesPerField.existsError('username','password')>
                            <p class="text-red-600 text-sm mt-1">${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}</p>
                        </#if>
                    </div>

                    <!-- Password -->
                    <div>
                        <div class="flex justify-between items-center mb-1">
                            <label for="password-desktop" class="block text-sm font-medium text-gray-700">${msg("password")}</label>
                            <#if realm.resetPasswordAllowed>
                                <a href="${url.loginResetCredentialsUrl}" class="text-sm text-purple-600 hover:text-purple-800 transition-colors">${msg("doForgotPassword")}</a>
                            </#if>
                        </div>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400">lock</span>
                            </div>
                            <input type="password" id="password-desktop" name="password"
                                   class="input-focus pl-10 pr-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full transition-all duration-200"
                                   placeholder="••••••••"
                                   autocomplete="current-password"/>
                            <button type="button" id="togglePasswordDesktop" class="absolute inset-y-0 right-0 pr-3 flex items-center">
                                <span class="material-symbols-outlined text-gray-400 hover:text-gray-600">visibility</span>
                            </button>
                        </div>
                    </div>

                    <!-- Remember Me -->
                    <#if realm.rememberMe>
                        <div class="flex items-center">
                            <input type="checkbox" id="rememberMe-desktop" name="rememberMe"
                                   class="checkbox h-4 w-4 text-purple-600 focus:ring-purple-500 border-gray-300 rounded"
                                   <#if login.rememberMe??>checked</#if>/>
                            <label for="rememberMe-desktop" class="ml-2 block text-sm text-gray-700">${msg("rememberMe")}</label>
                        </div>
                    </#if>

                    <!-- Submit -->
                    <div>
                        <input type="hidden" name="credentialId" id="id-hidden-input-desktop"
                               <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                        <button type="submit" id="kc-login-desktop"
                                class="animate-gradient w-full py-3 px-4 rounded-lg text-white font-medium btn-transition focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500">
                            ${msg("doLogIn")}
                        </button>
                    </div>
                </form>
            </div>

            <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                <p class="text-center text-sm text-gray-600 mt-4">
                    Don't have an account?
                    <a href="${url.registrationUrl}" class="text-purple-600 hover:underline">Create one</a>
                </p>
            </#if>
        </div>
    </div>
</div>

<!-- Mobile layout -->
<div class="mobile-layout mobile-container relative z-10">
    <!-- Mobile header -->
    <div class="mobile-header">
        <div class="text-center mobile-fade-in">
            <div class="inline-flex items-center justify-center mb-4">
                <div class="w-16 h-16">
                    <span class="material-symbols-outlined text-6xl text-purple-600">shopping_bag</span>
                </div>
            </div>
            <h1 class="text-4xl font-bold gradient-text mb-2">Pinepetosan Marketplace</h1>
            <p class="text-gray-600 text-lg">Welcome back</p>
        </div>
    </div>

    <!-- Mobile content -->
    <div class="mobile-content">
        <div class="mobile-form-container">
            <div class="mobile-glass-effect mobile-form-card mobile-fade-in" style="animation-delay: 0.2s">
                <form id="kc-form-login-mobile" action="${url.loginAction}" method="post" class="space-y-6">
                    <!-- Username/Email -->
                    <div class="mobile-slide-down" style="animation-delay: 0.3s">
                        <label for="username-mobile" class="block text-sm font-semibold text-gray-700 mb-3">
                            <#if !realm.loginWithEmailAllowed>${msg("username")}
                            <#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}
                            <#else>${msg("email")}</#if>
                        </label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">mail</span>
                            </div>
                            <input type="text" id="username-mobile" name="username"
                                   value="${(login.username!'')}"
                                   class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   autocomplete="username"
                                   placeholder="Enter your email"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
                        </div>
                        <#if messagesPerField.existsError('username','password')>
                            <p class="text-red-600 text-sm mt-2 font-medium">${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}</p>
                        </#if>
                    </div>

                    <!-- Password -->
                    <div class="mobile-slide-down" style="animation-delay: 0.4s">
                        <div class="flex justify-between items-center mb-3">
                            <label for="password-mobile" class="block text-sm font-semibold text-gray-700">${msg("password")}</label>
                            <#if realm.resetPasswordAllowed>
                                <a href="${url.loginResetCredentialsUrl}" class="text-sm text-purple-600 font-medium">Forgot?</a>
                            </#if>
                        </div>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">lock</span>
                            </div>
                            <input type="password" id="password-mobile" name="password"
                                   class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon pr-14 border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="Enter your password"
                                   autocomplete="current-password"/>
                            <button type="button" id="togglePasswordMobile" class="absolute inset-y-0 right-0 pr-4 flex items-center mobile-touch-target">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">visibility</span>
                            </button>
                        </div>
                    </div>

                    <!-- Remember Me -->
                    <#if realm.rememberMe>
                        <div class="flex items-center mobile-slide-down" style="animation-delay: 0.5s">
                            <input type="checkbox" id="rememberMe-mobile" name="rememberMe"
                                   class="h-5 w-5 text-purple-600 focus:ring-purple-500 border-gray-300 rounded"
                                   <#if login.rememberMe??>checked</#if>/>
                            <label for="rememberMe-mobile" class="ml-3 block text-sm text-gray-700 font-medium">${msg("rememberMe")}</label>
                        </div>
                    </#if>

                    <!-- Submit Button -->
                    <div class="mobile-slide-down" style="animation-delay: 0.6s">
                        <input type="hidden" name="credentialId" id="id-hidden-input-mobile"
                               <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                        <button type="submit" id="kc-login-mobile"
                                class="animate-gradient mobile-btn-active mobile-touch-target mobile-button w-full text-white focus:outline-none focus:ring-4 focus:ring-purple-300 transition-all duration-200">
                            ${msg("doLogIn")}
                        </button>
                    </div>
                </form>
            </div>

            <!-- Sign up link -->
            <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                <div class="text-center mt-8 mobile-slide-down" style="animation-delay: 0.7s">
                    <p class="text-gray-600">
                        Don't have an account?
                        <a href="${url.registrationUrl}" class="text-purple-600 font-semibold">Sign up</a>
                    </p>
                </div>
            </#if>

            <!-- Trust indicators -->
            <div class="mt-8 text-center mobile-slide-down" style="animation-delay: 0.8s">
                <div class="flex justify-center items-center space-x-4 mb-4">
                    <div class="flex items-center space-x-1">
                        <span class="material-symbols-outlined text-green-500 text-sm">verified</span>
                        <span class="text-xs text-gray-600 font-medium">Verified</span>
                    </div>
                    <div class="flex items-center space-x-1">
                        <span class="material-symbols-outlined text-purple-500 text-sm">security</span>
                        <span class="text-xs text-gray-600 font-medium">Secure</span>
                    </div>
                    <div class="flex items-center space-x-1">
                        <span class="material-symbols-outlined text-blue-500 text-sm">local_shipping</span>
                        <span class="text-xs text-gray-600 font-medium">Fast Delivery</span>
                    </div>
                </div>
                <p class="text-xs text-gray-500">
                    Nigeria's most trusted marketplace
                </p>
            </div>
        </div>
    </div>
</div>

<script>
    // Password toggle functionality for desktop
document.getElementById("togglePasswordDesktop").addEventListener("click", function () {
    const pwd = document.getElementById("password-desktop");
    const icon = this.querySelector("span");

    if (pwd.type === "password") {
        pwd.type = "text";
        icon.textContent = "visibility_off";
    } else {
        pwd.type = "password";
        icon.textContent = "visibility";
    }
});

// Password toggle functionality for mobile
document.getElementById("togglePasswordMobile").addEventListener("click", function () {
    const pwd = document.getElementById("password-mobile");
    const icon = this.querySelector("span");

    if (pwd.type === "password") {
        pwd.type = "text";
        icon.textContent = "visibility_off";
    } else {
        pwd.type = "password";
        icon.textContent = "visibility";
    }
});

// Enhanced form validation for both desktop and mobile
function setupFormValidation(formId, submitBtnId) {
    const form = document.getElementById(formId);
    const submitBtn = document.getElementById(submitBtnId);

    if (form && submitBtn) {
        form.addEventListener("submit", function(e) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="material-symbols-outlined animate-spin mr-2">progress_activity</span>Signing in...';
        });
    }
}

setupFormValidation("kc-form-login-desktop", "kc-login-desktop");
setupFormValidation("kc-form-login-mobile", "kc-login-mobile");

// Auto-focus first input on load
window.addEventListener("load", function() {
    const isDesktop = window.innerWidth >= 1024;
    const usernameInput = document.getElementById(isDesktop ? "username-desktop" : "username-mobile");
    if (usernameInput) {
        usernameInput.focus();
    }
});

</script>

</#if>
</@layout.registrationLayout>