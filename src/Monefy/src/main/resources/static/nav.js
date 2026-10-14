// Shared navigation bar. Every page loads this with <script src="nav.js" defer></script>.
// To add a page to the menu, add one line to `links`.
(function () {
    const links = [
        ['index.html', 'Home'],
        ['accounts.html', 'Accounts'],
        ['transactions.html', 'Transactions'],
        ['transfers.html', 'Transfers'],
        ['categories.html', 'Categories'],
        ['currencies.html', 'Currencies'],
        ['users.html', 'Users']
    ];

    const style = document.createElement('style');
    style.textContent = `
        .monefy-nav {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 4px;
            margin-bottom: 20px;
            padding: 8px 12px;
            background-color: #333;
            border-radius: 5px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.2);
            font-family: Arial, sans-serif;
        }
        .monefy-nav .monefy-brand {
            margin-right: 16px;
            color: #fff;
            font-size: 18px;
            font-weight: bold;
        }
        .monefy-nav a {
            padding: 8px 14px;
            color: #ddd;
            text-decoration: none;
            border-radius: 4px;
        }
        .monefy-nav a:hover {
            background-color: #555;
            color: #fff;
        }
        .monefy-nav a.active {
            background-color: #4CAF50;
            color: #fff;
        }
    `;
    document.head.appendChild(style);

    // "/api/v3/" and "/api/v3" both mean the home page
    const current = location.pathname.split('/').pop() || 'index.html';

    const nav = document.createElement('nav');
    nav.className = 'monefy-nav';

    const brand = document.createElement('span');
    brand.className = 'monefy-brand';
    brand.textContent = 'Monefy';
    nav.appendChild(brand);

    links.forEach(function (link) {
        const a = document.createElement('a');
        a.href = link[0];
        a.textContent = link[1];
        if (link[0] === current) {
            a.className = 'active';
        }
        nav.appendChild(a);
    });

    document.body.insertBefore(nav, document.body.firstChild);
})();
