document.addEventListener('DOMContentLoaded', () => {
    // Theme toggle functionality
    const themeToggles = document.querySelectorAll('.theme-toggle');
    const body = document.body;
    
    const updateThemeIcons = (isDarkMode) => {
        const iconName = isDarkMode ? 'light_mode' : 'dark_mode';
        themeToggles.forEach(toggle => {
            const icon = toggle.querySelector('.material-icons');
            if (icon) icon.textContent = iconName;
        });
    };

    // Check saved theme
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        body.classList.add('dark-mode');
        updateThemeIcons(true);
    } else {
        updateThemeIcons(false);
    }
    
    // Add event listeners to all theme toggles
    themeToggles.forEach(toggle => {
        toggle.addEventListener('click', (e) => {
            e.stopPropagation();
            const isDarkMode = !body.classList.contains('dark-mode');
            body.classList.toggle('dark-mode');
            
            updateThemeIcons(isDarkMode);
            localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
        });
    });

    // Mobile menu functionality
    const hamburgerMenu = document.querySelector('.hamburger-menu');
    const mobileMenu = document.querySelector('.mobile-menu');
    
    if (hamburgerMenu && mobileMenu) {
        hamburgerMenu.addEventListener('click', (e) => {
            e.stopPropagation();
            mobileMenu.classList.toggle('active');
            const menuIcon = hamburgerMenu.querySelector('.material-icons');
            menuIcon.textContent = mobileMenu.classList.contains('active') ? 'close' : 'menu';
        });

        // Close menu when clicking outside
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.mobile-menu') && !e.target.closest('.mobile-controls')) {
                mobileMenu.classList.remove('active');
                const menuIcon = hamburgerMenu.querySelector('.material-icons');
                if (menuIcon) menuIcon.textContent = 'menu';
            }
        });
    }

    // Smooth scrolling functionality
    document.querySelectorAll('.nav-link[data-target]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = link.getAttribute('data-target');
            const targetElement = document.getElementById(targetId);
            
            if (targetElement) {
                window.scrollTo({
                    top: targetId === 'top' ? 0 : targetElement.offsetTop - 64,
                    behavior: 'smooth'
                });
                
                // Close mobile menu if open
                if (mobileMenu && mobileMenu.classList.contains('active')) {
                    mobileMenu.classList.remove('active');
                    const menuIcon = hamburgerMenu.querySelector('.material-icons');
                    if (menuIcon) menuIcon.textContent = 'menu';
                }
            }
        });
    });

    // Intersection observer for gradient sections
    const gradientSections = document.querySelectorAll('.gradient-section');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, { threshold: 0.1 });
    
    gradientSections.forEach(section => {
        observer.observe(section);
    });

    // FAQ toggle functionality
    document.querySelectorAll('.faq-question').forEach(question => {
        question.addEventListener('click', () => {
            const faqItem = question.parentElement;
            const expandIcon = question.querySelector('.expand-icon');
            
            // Toggle the active class
            faqItem.classList.toggle('active');
            
            // Rotate the expand icon
            if (expandIcon) {
                expandIcon.style.transform = faqItem.classList.contains('active') 
                    ? 'rotate(180deg)' 
                    : 'rotate(0deg)';
            }
        });
    });

    // Update background gradients on theme change
    function updateBackgroundGradients() {
        const isDark = body.classList.contains('dark-mode');
        document.querySelectorAll('.background-gradients img').forEach(img => {
            const mode = img.getAttribute('data-mode');
            img.style.display = (isDark && mode === 'dark') || (!isDark && mode === 'light') ? 'block' : 'none';
        });
    }

    // Update app images based on theme
    function updateAppImages() {
        const isDark = body.classList.contains('dark-mode');
        document.querySelectorAll('.app-image').forEach(img => {
            const mode = img.getAttribute('data-mode');
            img.style.display = (isDark && mode === 'dark') || (!isDark && mode === 'light') ? 'block' : 'none';
        });
    }

    // Call these functions initially
    updateBackgroundGradients();
    updateAppImages();

    // Set up observers for theme changes
    const mutationObserver = new MutationObserver(() => {
        updateBackgroundGradients();
        updateAppImages();
    });
    mutationObserver.observe(body, { attributes: true, attributeFilter: ['class'] });
});

// Initialize functions that need to run on load
window.addEventListener('load', () => { 
});