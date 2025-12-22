const API_URL = "https://cuitibackmystere.onrender.com";
const container = document.getElementById("productsContainer");

async function cargarProductos() {
    container.innerHTML = "Cargando catálogo...";

    try {
        const res = await fetch(`${API_URL}/api/productos`);
        const data = await res.json();

        container.innerHTML = "";
        data.forEach(p => {
            container.innerHTML += `
                <div style="border:1px solid #ccc; margin:10px; padding:10px">
                    <h3>${p.nombre}</h3>
                    <p>$${p.precio}</p>
                </div>
            `;
        });
    } catch (e) {
        console.error(e);
        container.innerHTML = "Error cargando catálogo";
    }
}

document.addEventListener("DOMContentLoaded", cargarProductos);
