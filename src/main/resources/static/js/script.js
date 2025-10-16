(() => {
// Toast helper using Bootstrap 5
    window.showToast = (msg) => {
        const el = document.getElementById('liveToast');
        if (!el) return alert(msg);
        el.querySelector('.toast-body').textContent = msg;
        const toast = new bootstrap.Toast(el);
        toast.show();
    };


// Add to cart demo hook
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('[data-add-to-cart]');
        if (btn){
            e.preventDefault();
            const name = btn.getAttribute('data-name') || 'Sản phẩm';
            showToast(`${name} đã thêm vào giỏ!`);
// TODO: fetch('/cart/add', { method:'POST', body:... })
        }
    });
})();