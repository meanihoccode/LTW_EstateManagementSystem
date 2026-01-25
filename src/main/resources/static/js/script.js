const openBtn = document.getElementById("openLogin");
const modal = document.getElementById("loginModal");
const overlay = document.getElementById("overlay");
const closeBtn = document.querySelector(".close");

openBtn.onclick = () => {
    modal.style.display = "block";
    overlay.style.display = "block";
};

overlay.onclick = () => {
    modal.style.display = "none";
    overlay.style.display = "none";
};

closeBtn.onclick = () => {
    modal.style.display = "none";
    overlay.style.display = "none";
};

