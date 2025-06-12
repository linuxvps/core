// 📁 static/js/menu.js
function loadUserMenus() {
    fetch('/api/menus/my')
        .then(res => {
            if (!res.ok) throw new Error('خطا در دریافت منوها');
            return res.json();
        })
        .then(data => {
            const ul = document.getElementById("mainMenu");
            ul.innerHTML = '';
            data.sort((a, b) => a.orderIndex - b.orderIndex);
            data.forEach(menu => {
                const li = document.createElement("li");
                const a = document.createElement("a");
                a.href = menu.url;
                a.innerText = menu.title;
                li.appendChild(a);
                ul.appendChild(li);
            });
        })
        .catch(err => {
            console.error("⛔ خطا در بارگذاری منو:", err);
        });
}
