const links = document.querySelectorAll(".nav a");
const currentPath = window.location.pathname.split("/").pop();

links.forEach(link => {
    if (link.getAttribute("href") === currentPath) {
        link.classList.add("active");
    }
});


const overlay = document.getElementById("overlay");

const loginModal = document.getElementById("loginModal");
const contactModal = document.getElementById("contactModal");

const openLogin = document.getElementById("openLogin");
const openContact = document.getElementById("contactUs");

const closeLogin = document.getElementById("closeLogin");
const closeContact = document.getElementById("closeContact");

// Open login
openLogin.onclick = () => {
    loginModal.style.display = "block";
    overlay.style.display = "block";
};

// Open contact
openContact.onclick = () => {
    contactModal.style.display = "block";
    overlay.style.display = "block";
};

// Close login
closeLogin.onclick = () => {
    loginModal.style.display = "none";
    overlay.style.display = "none";
};

// Close contact
closeContact.onclick = () => {
    contactModal.style.display = "none";
    overlay.style.display = "none";
};

// Click overlay to close all
overlay.onclick = () => {
    loginModal.style.display = "none";
    contactModal.style.display = "none";
    overlay.style.display = "none";
};
