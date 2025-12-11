package com.mystere.mercadopago;

import com.mystere.mercadopago.model.AdminUser;
import com.mystere.mercadopago.model.Producto;
import com.mystere.mercadopago.repository.AdminUserRepository;
import com.mystere.mercadopago.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MercadopagoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MercadopagoApplication.class, args);
    }

    // 👇 Esto se ejecuta una vez al iniciar la app
    @Bean
    public CommandLineRunner initData(ProductoRepository productoRepository) {
        return args -> {
            if (productoRepository.count() == 0) {

                Producto p1 = new Producto();
                p1.setNombre("French Avenue - Liquid Brun");
                p1.setPrecio(48000);
                p1.setStock(10);
                p1.setImagen("img/frenchavenue-liquid-brun.png");
                p1.setGenero("hombre");
                p1.setSale(false);
                p1.setPrecioAntes(null);
                productoRepository.save(p1);

                Producto p2 = new Producto();
                p2.setNombre("Lattafa - Asad Bourbon");
                p2.setPrecio(41000);
                p2.setStock(10);
                p2.setImagen("img/lattafa-asad-bourbon.png");
                p2.setGenero("hombre");
                p2.setSale(true);// en oferta
                p2.setPrecioAntes(45000);
                productoRepository.save(p2);

                Producto p3 = new Producto();
                p3.setNombre("Afnan - 9PM");
                p3.setPrecio(50000);
                p3.setStock(10);
                p3.setImagen("img/afnan-9pm.png");
                p3.setGenero("hombre");
                p3.setSale(false);
                productoRepository.save(p3);

                Producto p4 = new Producto();
                p4.setNombre("Afnan - 9PM Rebel");
                p4.setPrecio(55000);
                p4.setStock(10);
                p4.setImagen("img/afnan-9pm-rebel.png");
                p4.setGenero("hombre");
                p4.setSale(false);
                productoRepository.save(p4);

                Producto p5 = new Producto();
                p5.setNombre("Al Haramain - Amber Oud Dubai Night");
                p5.setPrecio(73000);
                p5.setStock(10);
                p5.setImagen("img/alharamain-amberoud-dubai-night.png");
                p5.setGenero("unisex");
                p5.setSale(false);
                productoRepository.save(p5);

                Producto p6 = new Producto();
                p6.setNombre("Al Haramain - Amber Oud Aqua Dubai");
                p6.setPrecio(75000);
                p6.setStock(10);
                p6.setImagen("img/alharamain-amberoud-aqua-dubai.png");
                p6.setGenero("unisex");
                p6.setSale(true);
                productoRepository.save(p6);

                Producto p7 = new Producto();
                p7.setNombre("Armaf - Club De Nuit Intense Man");
                p7.setPrecio(51000);
                p7.setStock(10);
                p7.setImagen("img/armaf-clubdenuit-intenseman.png");
                p7.setGenero("hombre");
                p7.setSale(false);
                productoRepository.save(p7);

                Producto p8 = new Producto();
                p8.setNombre("Armaf - Club De Nuit Urban Elixir");
                p8.setPrecio(53000);
                p8.setStock(10);
                p8.setImagen("img/armaf-clubdenuit-urban-elixir.png");
                p8.setGenero("hombre");
                p8.setSale(false);
                productoRepository.save(p8);

                Producto p9 = new Producto();
                p9.setNombre("Armaf - Odyssey Mandarin Sky");
                p9.setPrecio(48000);
                p9.setStock(10);
                p9.setImagen("img/armaf-odyssey-mandarin-sky.png");
                p9.setGenero("unisex");
                p9.setSale(false);
                productoRepository.save(p9);

                Producto p10 = new Producto();
                p10.setNombre("Bharara - King");
                p10.setPrecio(52000);
                p10.setStock(10);
                p10.setImagen("img/bharara-king.png");
                p10.setGenero("hombre");
                p10.setSale(true);
                productoRepository.save(p10);

                System.out.println("✔ Productos iniciales cargados en la BD");
            }
        };
    }
}

