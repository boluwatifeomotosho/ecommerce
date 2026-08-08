<#import "template.ftl" as layout>
<#import "user-profile-commons.ftl" as userProfileCommons>
<@layout.registrationLayout displayMessage=messagesPerField.exists('global') displayRequiredFields=true; section>
<#if section = "header">
    <title>Pinepetosan Marketplace - Sign Up</title>
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

        .general-error {
            color: #dc2626 !important;
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

        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
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

        .animate-spin {
            animation: spin 1s linear infinite;
        }

        /* Mobile-specific animations */
        .mobile-fade-in {
            animation: fadeInUp 0.5s ease-out forwards;
        }

        .mobile-slide-down {
            animation: slideDown 0.3s ease-out forwards;
        }

        .mobile-shake {
            animation: shake 0.3s ease-in-out;
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

        .mobile-input-error {
            border-color: #dc2626;
            box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
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

        /* Progress indicator */
        .progress-step {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background-color: #e5e7eb;
            transition: background-color 0.3s ease;
        }

        .progress-step.active {
            background-color: #630ed4;
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
                padding-bottom: 1.5rem;
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
                padding: 1rem;
                border-radius: 0.5rem;
                border-width: 2px;
                font-size: 1.125rem;
            }

            .mobile-input-with-icon {
                padding-left: 3rem;
            }

            .mobile-button {
                padding: 1rem 1.5rem;
                border-radius: 1rem;
                font-size: 1.125rem;
                font-weight: 700;
            }

            .mobile-icon-container {
                padding-left: 1rem;
            }

            .mobile-icon {
                font-size: 1.125rem;
            }
        }
    </style>
<#elseif section = "form">

<body class="bg-gradient-to-br from-purple-50 to-indigo-50 min-h-screen"
      style="font-family: 'Plus Jakarta Sans', sans-serif">

<!-- Desktop background -->
<div class="desktop-background fixed top-0 left-0 w-full h-full overflow-hidden z-0">
    <div class="absolute -top-20 -left-20 w-72 h-72 bg-purple-200 rounded-full opacity-20 animate-float"></div>
    <div class="absolute top-1/4 -right-20 w-64 h-64 bg-indigo-200 rounded-full opacity-20 animate-float"></div>
    <div class="absolute bottom-20 left-1/4 w-56 h-56 bg-violet-200 rounded-full opacity-20 animate-float"></div>
    <div class="absolute bottom-0 right-1/4 w-48 h-48 bg-purple-300 rounded-full opacity-20 animate-float"></div>
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
            <h2 class="text-4xl font-bold mb-6">Join Nigeria's Marketplace</h2>
            <p class="text-lg opacity-90 mb-8 comtext">Start your journey with Nigeria's most trusted marketplace. Buy from verified vendors or grow your business by selling to millions.</p>
            <div class="space-y-6">
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">storefront</span>
                    </div>
                    <p class="text-lg comtext">Start selling in minutes</p>
                </div>
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">local_shipping</span>
                    </div>
                    <p class="text-lg comtext">Nationwide delivery network</p>
                </div>
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center mr-4">
                        <span class="material-symbols-outlined text-white">payments</span>
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
                Join thousands of successful Nigerian entrepreneurs on Pinepetosan Marketplace
            </p>
        </div>
    </div>

    <!-- Desktop registration form -->
    <div class="responsive-right-panel w-1/2 flex items-center justify-center p-8">
        <div class="w-full max-w-md">
            <div class="glass-effect rounded-2xl shadow-xl p-8 animate-fade-in">
                <h2 id="titleText-desktop" class="text-2xl font-bold text-gray-800 mb-2">Create Account</h2>
                <p id="subtitleText-desktop" class="text-gray-600 mb-6 text-sm">Join Nigeria's marketplace today</p>

                <!-- Account type toggle -->
                <div class="mode-toggle-desktop grid grid-cols-2 gap-0 bg-gray-100 p-1 rounded-lg mb-6" role="tablist" aria-label="Account type">
                    <button type="button" id="modeCustomer-desktop"
                            class="py-2.5 px-3 text-sm font-semibold rounded-md transition-all bg-white text-purple-600 shadow-sm"
                            role="tab" aria-selected="true">Customer</button>
                    <button type="button" id="modeVendor-desktop"
                            class="py-2.5 px-3 text-sm font-semibold rounded-md transition-all text-gray-500"
                            role="tab" aria-selected="false">Vendor</button>
                </div>

                <form id="kc-register-form-desktop" action="${url.registrationAction}" method="post" class="space-y-5">

                    <!-- Account type marker (persisted as Keycloak user attribute) -->
                    <input type="hidden" id="accountType-desktop" name="user.attributes.accountType" value="customer"/>

                    <!-- ========= CUSTOMER FIELDS ========= -->
                    <div id="customerFields-desktop">
                        <!-- First & Last Name -->
                        <div class="flex gap-4">
                            <div class="w-1/2">
                                <label for="firstName-desktop" class="block text-sm font-medium text-gray-700 mb-1">${msg("firstName")}</label>
                                <input type="text" id="firstName-desktop" name="firstName"
                                       class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full"
                                       placeholder="First Name"/>
                            </div>
                            <div class="w-1/2">
                                <label for="lastName-desktop" class="block text-sm font-medium text-gray-700 mb-1">${msg("lastName")}</label>
                                <input type="text" id="lastName-desktop" name="lastName"
                                       class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full"
                                       placeholder="Last Name"/>
                            </div>
                        </div>
                    </div>

                    <!-- ========= VENDOR FIELDS ========= -->
                    <div id="vendorFields-desktop" class="space-y-5" style="display:none">
                        <!-- Company Name -->
                        <div>
                            <label for="companyName-desktop" class="block text-sm font-medium text-gray-700 mb-1">Company name</label>
                            <input type="text" id="companyName-desktop" name="user.attributes.companyName"
                                   class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full"
                                   placeholder="e.g. Acme Trading Ltd"/>
                            <p id="companyName-error-desktop" class="text-red-500 text-xs mt-1 general-error hidden">Enter your company name.</p>
                        </div>

                        <!-- Phone -->
                        <div>
                            <label for="phone-desktop" class="block text-sm font-medium text-gray-700 mb-1">Contact phone number</label>
                            <input type="tel" id="phone-desktop" name="user.attributes.phoneNumber" inputmode="tel"
                                   class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full"
                                   placeholder="+2348012345678 or 08012345678"/>
                            <p id="phone-error-desktop" class="text-red-500 text-xs mt-1 general-error hidden">Enter a valid Nigerian phone number.</p>
                        </div>

                        <!-- Website -->
                        <div>
                            <label for="websiteUrl-desktop" class="block text-sm font-medium text-gray-700 mb-1">Website URL</label>
                            <input type="url" id="websiteUrl-desktop" name="user.attributes.websiteUrl"
                                   class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full"
                                   placeholder="https://your-company.com"/>
                            <p id="website-error-desktop" class="text-red-500 text-xs mt-1 general-error hidden">Enter a valid URL (must start with http:// or https://).</p>
                        </div>

                        <!-- Hidden firstName/lastName so Keycloak's built-in validators still pass.
                             We mirror companyName into firstName on submit. -->
                        <input type="hidden" id="vendorFirstName-desktop" name="firstName" value=""/>
                        <input type="hidden" name="lastName" value="."/>
                    </div>

                    <!-- Email -->
                    <div>
                        <label for="email-desktop" class="block text-sm font-medium text-gray-700 mb-1">${msg("email")}</label>
                        <input type="email" id="email-desktop" name="email"
                               class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full"
                               placeholder="you@example.com" autocomplete="email" required/>
                        <p id="email-error-desktop" class="text-red-500 text-xs mt-1 general-error hidden">Please enter a valid email address.</p>
                    </div>

                    <!-- Password -->
                    <div class="relative">
                        <label for="password-desktop" class="block text-sm font-medium text-gray-700 mb-1">${msg("password")}</label>
                        <input type="password" id="password-desktop" name="password"
                               class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full pr-10"
                               placeholder="••••••••" autocomplete="new-password" required/>
                        <button type="button" id="toggle-password-desktop"
                                class="absolute top-1/2 right-3 -translate-y-1/2 text-gray-500">
                            <span class="material-symbols-outlined">visibility</span>
                        </button>
                        <p id="password-error-desktop" class="text-red-500 text-xs mt-1 general-error hidden">
                            Must be at least 8 characters, include uppercase, lowercase, and a special character.
                        </p>
                    </div>

                    <!-- Confirm Password -->
                    <div class="relative">
                        <label for="password-confirm-desktop" class="block text-sm font-medium text-gray-700 mb-1">${msg("passwordConfirm")}</label>
                        <input type="password" id="password-confirm-desktop" name="password-confirm"
                               class="input-focus px-4 py-3 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 w-full pr-10"
                               placeholder="••••••••" autocomplete="new-password" required/>
                        <button type="button" id="toggle-password-confirm-desktop"
                                class="absolute top-1/2 right-3 -translate-y-1/2 text-gray-500">
                            <span class="material-symbols-outlined">visibility</span>
                        </button>
                        <p id="password-confirm-error-desktop" class="text-red-500 text-xs mt-1 general-error hidden">
                            Passwords do not match.
                        </p>
                    </div>

                    <!-- Submit -->
                    <button type="submit" id="register-btn-desktop"
                            class="animate-gradient w-full py-3 px-4 rounded-lg text-white font-medium btn-transition focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 opacity-50 cursor-not-allowed">
                        ${msg("doRegister")}
                    </button>

                    <p class="text-center text-sm text-gray-600 mt-4">
                        Already have an account? <a href="${url.loginUrl}" class="text-purple-600 hover:underline">Login</a>
                    </p>
                </form>
            </div>
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
            <p class="text-gray-600 text-lg">Create your account</p>
        </div>
    </div>

    <!-- Progress indicator -->
    <div class="flex justify-center px-6 mb-6">
        <div class="flex space-x-2">
            <div class="progress-step active" id="step-1"></div>
            <div class="progress-step" id="step-2"></div>
            <div class="progress-step" id="step-3"></div>
            <div class="progress-step" id="step-4"></div>
        </div>
    </div>

    <!-- Mobile content -->
    <div class="mobile-content">
        <div class="mobile-form-container">
            <div class="mobile-glass-effect mobile-form-card mobile-fade-in" style="animation-delay: 0.2s">

                <!-- Account type toggle -->
                <div class="mode-toggle-mobile grid grid-cols-2 gap-0 bg-gray-100 p-1 rounded-xl mb-6" role="tablist" aria-label="Account type">
                    <button type="button" id="modeCustomer-mobile"
                            class="py-3 px-3 text-sm font-semibold rounded-lg transition-all bg-white text-purple-600 shadow-sm"
                            role="tab" aria-selected="true">Customer</button>
                    <button type="button" id="modeVendor-mobile"
                            class="py-3 px-3 text-sm font-semibold rounded-lg transition-all text-gray-500"
                            role="tab" aria-selected="false">Vendor</button>
                </div>

                <form id="kc-register-form-mobile" action="${url.registrationAction}" method="post" class="space-y-6">

                    <!-- Account type marker -->
                    <input type="hidden" id="accountType-mobile" name="user.attributes.accountType" value="customer"/>

                    <!-- ========= CUSTOMER FIELDS ========= -->
                    <div id="customerFields-mobile" class="space-y-6">
                        <!-- First Name -->
                        <div class="mobile-slide-down" style="animation-delay: 0.3s">
                            <label for="firstName-mobile" class="block text-sm font-semibold text-gray-700 mb-3">${msg("firstName")}</label>
                            <input type="text" id="firstName-mobile" name="firstName"
                                   class="mobile-input-focus mobile-touch-target mobile-input border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="First name"
                                   autocomplete="given-name"/>
                            <p id="firstName-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">Please enter your first name.</p>
                        </div>

                        <!-- Last Name -->
                        <div class="mobile-slide-down" style="animation-delay: 0.4s">
                            <label for="lastName-mobile" class="block text-sm font-semibold text-gray-700 mb-3">${msg("lastName")}</label>
                            <input type="text" id="lastName-mobile" name="lastName"
                                   class="mobile-input-focus mobile-touch-target mobile-input border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="Last name"
                                   autocomplete="family-name"/>
                            <p id="lastName-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">Please enter your last name.</p>
                        </div>
                    </div>

                    <!-- ========= VENDOR FIELDS ========= -->
                    <div id="vendorFields-mobile" class="space-y-6" style="display:none">
                        <!-- Company Name -->
                        <div class="mobile-slide-down" style="animation-delay: 0.3s">
                            <label for="companyName-mobile" class="block text-sm font-semibold text-gray-700 mb-3">Company name</label>
                            <input type="text" id="companyName-mobile" name="user.attributes.companyName"
                                   class="mobile-input-focus mobile-touch-target mobile-input border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="e.g. Acme Trading Ltd"/>
                            <p id="companyName-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">Enter your company name.</p>
                        </div>

                        <!-- Phone -->
                        <div class="mobile-slide-down" style="animation-delay: 0.4s">
                            <label for="phone-mobile" class="block text-sm font-semibold text-gray-700 mb-3">Contact phone number</label>
                            <div class="relative">
                                <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                    <span class="material-symbols-outlined text-gray-400 mobile-icon">call</span>
                                </div>
                                <input type="tel" id="phone-mobile" name="user.attributes.phoneNumber" inputmode="tel"
                                       class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon border-gray-200 focus:outline-none w-full transition-all duration-200"
                                       placeholder="+2348012345678"/>
                            </div>
                            <p id="phone-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">Enter a valid Nigerian phone number.</p>
                        </div>

                        <!-- Website -->
                        <div class="mobile-slide-down" style="animation-delay: 0.45s">
                            <label for="websiteUrl-mobile" class="block text-sm font-semibold text-gray-700 mb-3">Website URL</label>
                            <div class="relative">
                                <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                    <span class="material-symbols-outlined text-gray-400 mobile-icon">language</span>
                                </div>
                                <input type="url" id="websiteUrl-mobile" name="user.attributes.websiteUrl"
                                       class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon border-gray-200 focus:outline-none w-full transition-all duration-200"
                                       placeholder="https://your-company.com"/>
                            </div>
                            <p id="website-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">Enter a valid URL (http:// or https://).</p>
                        </div>

                        <!-- Hidden firstName/lastName so Keycloak's built-in validators still pass -->
                        <input type="hidden" id="vendorFirstName-mobile" name="firstName" value=""/>
                        <input type="hidden" name="lastName" value="."/>
                    </div>

                    <!-- Email -->
                    <div class="mobile-slide-down" style="animation-delay: 0.5s">
                        <label for="email-mobile" class="block text-sm font-semibold text-gray-700 mb-3">${msg("email")}</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">mail</span>
                            </div>
                            <input type="email" id="email-mobile" name="email"
                                   class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="your@email.com"
                                   autocomplete="email"
                                   required/>
                        </div>
                        <p id="email-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">Please enter a valid email address.</p>
                    </div>

                    <!-- Password -->
                    <div class="mobile-slide-down" style="animation-delay: 0.6s">
                        <label for="password-mobile" class="block text-sm font-semibold text-gray-700 mb-3">${msg("password")}</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">lock</span>
                            </div>
                            <input type="password" id="password-mobile" name="password"
                                   class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon pr-14 border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="Create password"
                                   autocomplete="new-password"
                                   required/>
                            <button type="button" id="toggle-password-mobile" class="absolute inset-y-0 right-0 pr-4 flex items-center mobile-touch-target">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">visibility</span>
                            </button>
                        </div>
                        <p id="password-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">
                            Password must be at least 8 characters with uppercase, lowercase, and special character.
                        </p>
                        <!-- Password strength indicator -->
                        <div class="mt-2">
                            <div class="flex space-x-1">
                                <div class="h-1 flex-1 bg-gray-200 rounded" id="strength-1"></div>
                                <div class="h-1 flex-1 bg-gray-200 rounded" id="strength-2"></div>
                                <div class="h-1 flex-1 bg-gray-200 rounded" id="strength-3"></div>
                                <div class="h-1 flex-1 bg-gray-200 rounded" id="strength-4"></div>
                            </div>
                            <p id="strength-text" class="text-xs text-gray-500 mt-1">Password strength</p>
                        </div>
                    </div>

                    <!-- Confirm Password -->
                    <div class="mobile-slide-down" style="animation-delay: 0.7s">
                        <label for="password-confirm-mobile" class="block text-sm font-semibold text-gray-700 mb-3">${msg("passwordConfirm")}</label>
                        <div class="relative">
                            <div class="absolute inset-y-0 left-0 mobile-icon-container flex items-center pointer-events-none">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">lock</span>
                            </div>
                            <input type="password" id="password-confirm-mobile" name="password-confirm"
                                   class="mobile-input-focus mobile-touch-target mobile-input mobile-input-with-icon pr-14 border-gray-200 focus:outline-none w-full transition-all duration-200"
                                   placeholder="Confirm password"
                                   autocomplete="new-password"
                                   required/>
                            <button type="button" id="toggle-password-confirm-mobile" class="absolute inset-y-0 right-0 pr-4 flex items-center mobile-touch-target">
                                <span class="material-symbols-outlined text-gray-400 mobile-icon">visibility</span>
                            </button>
                        </div>
                        <p id="password-confirm-error-mobile" class="text-red-600 text-sm mt-2 font-medium general-error hidden">
                            Passwords do not match.
                        </p>
                    </div>

                    <!-- Submit Button -->
                    <div class="mobile-slide-down" style="animation-delay: 0.8s">
                        <button type="submit" id="register-btn-mobile"
                                class="animate-gradient mobile-btn-active mobile-touch-target mobile-button w-full text-white focus:outline-none focus:ring-4 focus:ring-purple-300 transition-all duration-200 opacity-50 cursor-not-allowed"
                                disabled>
                            ${msg("doRegister")}
                        </button>
                    </div>
                </form>
            </div>

            <!-- Login link -->
            <div class="text-center mt-8 mobile-slide-down" style="animation-delay: 0.9s">
                <p class="text-gray-600">
                    Already have an account?
                    <a href="${url.loginUrl}" class="text-purple-600 font-semibold">Sign in</a>
                </p>
            </div>

            <!-- Benefits -->
            <div class="mt-8 space-y-3 mobile-slide-down" style="animation-delay: 1s">
                <div class="flex items-center space-x-3 text-sm text-gray-600">
                    <span class="material-symbols-outlined text-green-500 text-sm">check_circle</span>
                    <span>Start selling immediately</span>
                </div>
                <div class="flex items-center space-x-3 text-sm text-gray-600">
                    <span class="material-symbols-outlined text-green-500 text-sm">check_circle</span>
                    <span>Access to millions of customers</span>
                </div>
                <div class="flex items-center space-x-3 text-sm text-gray-600">
                    <span class="material-symbols-outlined text-green-500 text-sm">check_circle</span>
                    <span>Secure payment processing</span>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // ================================================================
    // Shared helpers
    // ================================================================
    const PHONE_REGEX   = /^(\+234\d{10}|0\d{10})$/;
    const WEBSITE_REGEX = /^https?:\/\/[^\s.]+\.[^\s]{2,}$/;

    function readInitialMode() {
        const params = new URLSearchParams(window.location.search);
        const fromQuery = (params.get('account_type') || '').toLowerCase();
        if (fromQuery === 'vendor' || fromQuery === 'customer') {
            try { sessionStorage.setItem('nm_account_type', fromQuery); } catch (e) {}
            return fromQuery;
        }

        // Keycloak strips unknown query params on internal redirects,
        // but the browser preserves the URL fragment across 30x redirects.
        const rawHash = (window.location.hash || '').replace(/^#/, '');
        const hashParams = new URLSearchParams(rawHash);
        const fromHash = (hashParams.get('account_type') || '').toLowerCase();
        if (fromHash === 'vendor' || fromHash === 'customer') {
            try { sessionStorage.setItem('nm_account_type', fromHash); } catch (e) {}
            return fromHash;
        }

        // Keycloak may 302 again (e.g. after form re-post) and drop the hash too.
        // Fall back to a value we stashed earlier in this browser session.
        try {
            const cached = (sessionStorage.getItem('nm_account_type') || '').toLowerCase();
            if (cached === 'vendor' || cached === 'customer') return cached;
        } catch (e) {}

        return 'customer';
    }

    // ================================================================
    // Desktop form
    // ================================================================
    function setupDesktopValidation() {
        const form            = document.getElementById('kc-register-form-desktop');
        const emailInput      = document.getElementById('email-desktop');
        const emailError      = document.getElementById('email-error-desktop');
        const passwordInput   = document.getElementById('password-desktop');
        const passwordError   = document.getElementById('password-error-desktop');
        const confirmInput    = document.getElementById('password-confirm-desktop');
        const confirmError    = document.getElementById('password-confirm-error-desktop');
        const togglePassword  = document.getElementById('toggle-password-desktop');
        const togglePwConfirm = document.getElementById('toggle-password-confirm-desktop');
        const registerBtn     = document.getElementById('register-btn-desktop');

        const titleEl         = document.getElementById('titleText-desktop');
        const subtitleEl      = document.getElementById('subtitleText-desktop');

        // Mode toggle
        const modeCustomerBtn = document.getElementById('modeCustomer-desktop');
        const modeVendorBtn   = document.getElementById('modeVendor-desktop');
        const accountTypeHid  = document.getElementById('accountType-desktop');
        const customerFields  = document.getElementById('customerFields-desktop');
        const vendorFields    = document.getElementById('vendorFields-desktop');

        // Customer inputs
        const firstNameInput  = document.getElementById('firstName-desktop');
        const lastNameInput   = document.getElementById('lastName-desktop');

        // Vendor inputs
        const companyInput    = document.getElementById('companyName-desktop');
        const companyError    = document.getElementById('companyName-error-desktop');
        const phoneInput      = document.getElementById('phone-desktop');
        const phoneError      = document.getElementById('phone-error-desktop');
        const websiteInput    = document.getElementById('websiteUrl-desktop');
        const websiteError    = document.getElementById('website-error-desktop');
        const vendorFirstMirror = document.getElementById('vendorFirstName-desktop');

        let mode = 'customer';

        function setDisabled(container, disabled) {
            if (!container) return;
            container.querySelectorAll('input, select, textarea').forEach(el => {
                el.disabled = disabled;
            });
        }

        function applyMode(newMode) {
            mode = newMode;
            accountTypeHid.value = newMode;

            const isVendor = newMode === 'vendor';
            customerFields.style.display = isVendor ? 'none' : '';
            vendorFields.style.display   = isVendor ? '' : 'none';

            // Disabled inputs are NOT submitted with the form — prevents duplicate firstName/lastName
            setDisabled(customerFields, isVendor);
            setDisabled(vendorFields, !isVendor);

            // Toggle button styling
            modeCustomerBtn.classList.toggle('bg-white', !isVendor);
            modeCustomerBtn.classList.toggle('text-purple-600', !isVendor);
            modeCustomerBtn.classList.toggle('shadow-sm', !isVendor);
            modeCustomerBtn.classList.toggle('text-gray-500', isVendor);
            modeCustomerBtn.setAttribute('aria-selected', String(!isVendor));

            modeVendorBtn.classList.toggle('bg-white', isVendor);
            modeVendorBtn.classList.toggle('text-purple-600', isVendor);
            modeVendorBtn.classList.toggle('shadow-sm', isVendor);
            modeVendorBtn.classList.toggle('text-gray-500', !isVendor);
            modeVendorBtn.setAttribute('aria-selected', String(isVendor));

            if (titleEl)    titleEl.textContent    = isVendor ? 'Register your company' : 'Create Account';
            if (subtitleEl) subtitleEl.textContent = isVendor ? 'List your business on Pinepetosan Marketplace' : "Join Pinepetosan Marketplace today";

            validateFormDesktop();
        }

        modeCustomerBtn.addEventListener('click', () => applyMode('customer'));
        modeVendorBtn.addEventListener('click',   () => applyMode('vendor'));

        // Email
        emailInput.addEventListener('input', () => {
            const valid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailInput.value);
            emailError.classList.toggle('hidden', valid || emailInput.value === '');
        });

        // Password
        passwordInput.addEventListener('input', () => {
            const valid = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$/.test(passwordInput.value);
            passwordError.classList.toggle('hidden', valid || passwordInput.value === '');
            checkConfirmPasswordDesktop();
        });

        confirmInput.addEventListener('input', checkConfirmPasswordDesktop);
        function checkConfirmPasswordDesktop() {
            const matches = confirmInput.value === passwordInput.value;
            confirmError.classList.toggle('hidden', matches || confirmInput.value === '');
        }

        togglePassword.addEventListener('click', () => {
            const type = passwordInput.type === 'password' ? 'text' : 'password';
            passwordInput.type = type;
            togglePassword.innerHTML = type === 'password'
                ? '<span class="material-symbols-outlined">visibility</span>'
                : '<span class="material-symbols-outlined">visibility_off</span>';
        });
        togglePwConfirm.addEventListener('click', () => {
            const type = confirmInput.type === 'password' ? 'text' : 'password';
            confirmInput.type = type;
            togglePwConfirm.innerHTML = type === 'password'
                ? '<span class="material-symbols-outlined">visibility</span>'
                : '<span class="material-symbols-outlined">visibility_off</span>';
        });

        // Vendor field listeners
        companyInput.addEventListener('input', () => {
            const valid = companyInput.value.trim().length > 0;
            companyError.classList.toggle('hidden', valid || companyInput.value === '');
        });
        phoneInput.addEventListener('input', () => {
            const valid = PHONE_REGEX.test(phoneInput.value.trim());
            phoneError.classList.toggle('hidden', valid || phoneInput.value === '');
        });
        websiteInput.addEventListener('input', () => {
            const valid = WEBSITE_REGEX.test(websiteInput.value.trim());
            websiteError.classList.toggle('hidden', valid || websiteInput.value === '');
        });
        function validateFormDesktop() {
            const emailValid    = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailInput.value);
            const passwordValid = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$/.test(passwordInput.value);
            const confirmValid  = confirmInput.value === passwordInput.value && passwordValid;

            let identityValid;
            if (mode === 'vendor') {
                const companyValid = companyInput.value.trim().length > 0;
                const phoneValid   = PHONE_REGEX.test(phoneInput.value.trim());
                const websiteValid = WEBSITE_REGEX.test(websiteInput.value.trim());
                identityValid = companyValid && phoneValid && websiteValid;
            } else {
                identityValid = firstNameInput.value.trim() !== '' && lastNameInput.value.trim() !== '';
            }

            const formValid = emailValid && passwordValid && confirmValid && identityValid;
            registerBtn.disabled = !formValid;
            registerBtn.classList.toggle('opacity-50', !formValid);
            registerBtn.classList.toggle('cursor-not-allowed', !formValid);
        }

        [emailInput, passwordInput, confirmInput,
         firstNameInput, lastNameInput,
         companyInput, phoneInput, websiteInput]
        .forEach(input => input && input.addEventListener('input', validateFormDesktop));

        // Submit
        form.addEventListener('submit', (e) => {
            const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailInput.value);
            const pwOk    = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$/.test(passwordInput.value);
            const cfOk    = confirmInput.value === passwordInput.value;

            let identityOk;
            if (mode === 'vendor') {
                const companyOk = companyInput.value.trim().length > 0;
                const phoneOk   = PHONE_REGEX.test(phoneInput.value.trim());
                const websiteOk = WEBSITE_REGEX.test(websiteInput.value.trim());
                identityOk = companyOk && phoneOk && websiteOk;

                // Mirror companyName into firstName so Keycloak's built-in validator passes
                if (identityOk) vendorFirstMirror.value = companyInput.value.trim();
            } else {
                identityOk = firstNameInput.value.trim() !== '' && lastNameInput.value.trim() !== '';
            }

            if (!emailOk || !pwOk || !cfOk || !identityOk) {
                e.preventDefault();
                emailInput.dispatchEvent(new Event('input'));
                passwordInput.dispatchEvent(new Event('input'));
                confirmInput.dispatchEvent(new Event('input'));
                if (mode === 'vendor') {
                    companyInput.dispatchEvent(new Event('input'));
                    phoneInput.dispatchEvent(new Event('input'));
                    websiteInput.dispatchEvent(new Event('input'));
                }
            }
        });

        // Init mode from URL / repopulated form value
        const repopulated = (accountTypeHid.value || '').toLowerCase();
        const initial = (repopulated === 'vendor' || repopulated === 'customer')
            ? repopulated
            : readInitialMode();
        applyMode(initial);
    }

    // ================================================================
    // Mobile form
    // ================================================================
    function setupMobileValidation() {
        const form            = document.getElementById('kc-register-form-mobile');
        const firstNameInput  = document.getElementById('firstName-mobile');
        const lastNameInput   = document.getElementById('lastName-mobile');
        const emailInput      = document.getElementById('email-mobile');
        const passwordInput   = document.getElementById('password-mobile');
        const confirmInput    = document.getElementById('password-confirm-mobile');
        const registerBtn     = document.getElementById('register-btn-mobile');

        const firstNameError  = document.getElementById('firstName-error-mobile');
        const lastNameError   = document.getElementById('lastName-error-mobile');
        const emailError      = document.getElementById('email-error-mobile');
        const passwordError   = document.getElementById('password-error-mobile');
        const confirmError    = document.getElementById('password-confirm-error-mobile');

        // Mode toggle
        const modeCustomerBtn = document.getElementById('modeCustomer-mobile');
        const modeVendorBtn   = document.getElementById('modeVendor-mobile');
        const accountTypeHid  = document.getElementById('accountType-mobile');
        const customerFields  = document.getElementById('customerFields-mobile');
        const vendorFields    = document.getElementById('vendorFields-mobile');

        // Vendor inputs (mobile)
        const companyInput    = document.getElementById('companyName-mobile');
        const companyError    = document.getElementById('companyName-error-mobile');
        const phoneInput      = document.getElementById('phone-mobile');
        const phoneError      = document.getElementById('phone-error-mobile');
        const websiteInput    = document.getElementById('websiteUrl-mobile');
        const websiteError    = document.getElementById('website-error-mobile');
        const vendorFirstMirror = document.getElementById('vendorFirstName-mobile');

        const steps = [
            document.getElementById('step-1'),
            document.getElementById('step-2'),
            document.getElementById('step-3'),
            document.getElementById('step-4')
        ];

        let mode = 'customer';

        function setDisabled(container, disabled) {
            if (!container) return;
            container.querySelectorAll('input, select, textarea').forEach(el => {
                el.disabled = disabled;
            });
        }

        function applyMode(newMode) {
            mode = newMode;
            accountTypeHid.value = newMode;

            const isVendor = newMode === 'vendor';
            customerFields.style.display = isVendor ? 'none' : '';
            vendorFields.style.display   = isVendor ? '' : 'none';

            setDisabled(customerFields, isVendor);
            setDisabled(vendorFields, !isVendor);

            modeCustomerBtn.classList.toggle('bg-white', !isVendor);
            modeCustomerBtn.classList.toggle('text-purple-600', !isVendor);
            modeCustomerBtn.classList.toggle('shadow-sm', !isVendor);
            modeCustomerBtn.classList.toggle('text-gray-500', isVendor);
            modeCustomerBtn.setAttribute('aria-selected', String(!isVendor));

            modeVendorBtn.classList.toggle('bg-white', isVendor);
            modeVendorBtn.classList.toggle('text-purple-600', isVendor);
            modeVendorBtn.classList.toggle('shadow-sm', isVendor);
            modeVendorBtn.classList.toggle('text-gray-500', !isVendor);
            modeVendorBtn.setAttribute('aria-selected', String(isVendor));

            validateForm();
        }

        modeCustomerBtn.addEventListener('click', () => applyMode('customer'));
        modeVendorBtn.addEventListener('click',   () => applyMode('vendor'));

        // Password toggles
        document.getElementById('toggle-password-mobile').addEventListener('click', function() {
            togglePasswordVisibility('password-mobile', this);
        });
        document.getElementById('toggle-password-confirm-mobile').addEventListener('click', function() {
            togglePasswordVisibility('password-confirm-mobile', this);
        });
        function togglePasswordVisibility(inputId, button) {
            const input = document.getElementById(inputId);
            const icon = button.querySelector('span');
            if (input.type === 'password') { input.type = 'text';  icon.textContent = 'visibility_off'; }
            else                            { input.type = 'password'; icon.textContent = 'visibility'; }
        }

        function toggleError(input, errorElement, isValid) {
            if (isValid || input.value === '') {
                input.classList.remove('mobile-input-error');
                errorElement.classList.add('hidden');
            } else {
                input.classList.add('mobile-input-error', 'mobile-shake');
                errorElement.classList.remove('hidden');
                setTimeout(() => input.classList.remove('mobile-shake'), 300);
            }
        }

        function updateProgress(stepIndex, isValid) {
            if (!steps[stepIndex]) return;
            if (isValid) steps[stepIndex].classList.add('active');
            else         steps[stepIndex].classList.remove('active');
        }

        function updatePasswordStrength(password) {
            const strengthBars = [
                document.getElementById('strength-1'),
                document.getElementById('strength-2'),
                document.getElementById('strength-3'),
                document.getElementById('strength-4')
            ];
            const strengthText = document.getElementById('strength-text');

            let strength = 0;
            if (password.length >= 8) strength++;
            if (/[a-z]/.test(password)) strength++;
            if (/[A-Z]/.test(password)) strength++;
            if (/[^a-zA-Z0-9]/.test(password)) strength++;

            const colors = ['bg-red-400', 'bg-orange-400', 'bg-yellow-400', 'bg-green-400'];
            const texts  = ['Weak', 'Fair', 'Good', 'Strong'];

            strengthBars.forEach((bar, index) => {
                bar.className = 'h-1 flex-1 rounded ' + (index < strength ? colors[strength - 1] : 'bg-gray-200');
            });

            strengthText.textContent = password.length > 0 ? texts[strength - 1] || 'Too weak' : 'Password strength';
            strengthText.className = 'text-xs mt-1 ' + (strength >= 3 ? 'text-green-600' : strength >= 2 ? 'text-yellow-600' : 'text-red-600');
        }

        // Customer validators
        function validateFirstName() {
            const valid = firstNameInput.value.trim().length > 0;
            toggleError(firstNameInput, firstNameError, valid);
            updateProgress(0, valid);
            return valid;
        }
        function validateLastName() {
            const valid = lastNameInput.value.trim().length > 0;
            toggleError(lastNameInput, lastNameError, valid);
            updateProgress(1, valid);
            return valid;
        }

        // Vendor validators
        function validateCompany() {
            const valid = companyInput.value.trim().length > 0;
            toggleError(companyInput, companyError, valid);
            updateProgress(0, valid);
            return valid;
        }
        function validatePhone() {
            const valid = PHONE_REGEX.test(phoneInput.value.trim());
            toggleError(phoneInput, phoneError, valid);
            return valid;
        }
        function validateWebsite() {
            const valid = WEBSITE_REGEX.test(websiteInput.value.trim());
            toggleError(websiteInput, websiteError, valid);
            updateProgress(1, valid);
            return valid;
        }

        // Shared validators
        function validateEmail() {
            const valid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailInput.value);
            toggleError(emailInput, emailError, valid);
            updateProgress(2, valid);
            return valid;
        }
        function validatePassword() {
            const password = passwordInput.value;
            const valid = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$/.test(password);
            toggleError(passwordInput, passwordError, valid);
            updatePasswordStrength(password);
            return valid;
        }
        function validateConfirmPassword() {
            const valid = confirmInput.value === passwordInput.value && passwordInput.value.length > 0;
            toggleError(confirmInput, confirmError, valid);
            updateProgress(3, valid);
            return valid;
        }

        function validateForm() {
            const emailValid   = validateEmail();
            const passValid    = validatePassword();
            const confirmValid = validateConfirmPassword();

            let identityValid;
            if (mode === 'vendor') {
                const companyValid = validateCompany();
                const phoneValid   = validatePhone();
                const websiteValid = validateWebsite();
                identityValid = companyValid && phoneValid && websiteValid;
            } else {
                const firstOk = validateFirstName();
                const lastOk  = validateLastName();
                identityValid = firstOk && lastOk;
            }

            const formValid = emailValid && passValid && confirmValid && identityValid;
            registerBtn.disabled = !formValid;
            registerBtn.classList.toggle('opacity-50', !formValid);
            registerBtn.classList.toggle('cursor-not-allowed', !formValid);
            return formValid;
        }

        // Listeners
        [firstNameInput, lastNameInput, emailInput, passwordInput, confirmInput,
         companyInput, phoneInput, websiteInput]
        .forEach(input => input && input.addEventListener('input', validateForm));

        // Submit
        form.addEventListener('submit', function(e) {
            if (mode === 'vendor') {
                // Mirror companyName into firstName for Keycloak's built-in validator
                if (companyInput.value.trim().length > 0) {
                    vendorFirstMirror.value = companyInput.value.trim();
                }
            }
            if (!validateForm()) {
                e.preventDefault();
                return;
            }
            registerBtn.disabled = true;
            registerBtn.innerHTML = '<span class="material-symbols-outlined animate-spin mr-2">progress_activity</span>Creating account...';
        });

        // Init mode
        const repopulated = (accountTypeHid.value || '').toLowerCase();
        const initial = (repopulated === 'vendor' || repopulated === 'customer')
            ? repopulated
            : readInitialMode();
        applyMode(initial);
    }

    // Initialize appropriate validation based on screen size
    window.addEventListener('load', function() {
        const isDesktop = window.innerWidth >= 1024;

        if (isDesktop) {
            setupDesktopValidation();
            const firstDesk = document.getElementById('firstName-desktop');
            if (firstDesk && firstDesk.offsetParent !== null) firstDesk.focus();
            else {
                const companyDesk = document.getElementById('companyName-desktop');
                if (companyDesk) companyDesk.focus();
            }
        } else {
            setupMobileValidation();
            const firstMob = document.getElementById('firstName-mobile');
            if (firstMob && firstMob.offsetParent !== null) firstMob.focus();
            else {
                const companyMob = document.getElementById('companyName-mobile');
                if (companyMob) companyMob.focus();
            }
        }
    });
</script>

</#if>
</@layout.registrationLayout>