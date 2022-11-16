package com.bolsadeideas.springboot.reactor.app;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner{

	private static final Logger Log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		Flux<Usuario> nombres = Flux.just("Andres Guzman", "Pedro Fulan", "Jhon Doe", "Diego Sultano", "Juan Medrano", "Juan Mengano", "Bruce Lee", "Bruce Willies")
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase())) // Realiza la transformacion del stream
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce"))
				.doOnNext(
						usuario -> {
							if(usuario == null) {
								throw new RuntimeException("Nombres no pueden ser vacíos");
							}else {
								System.out.println(usuario.getNombre() + " " + usuario.getApellido());									
							}
						})
				.map(usuario -> {
						String nombre = usuario.getNombre().toLowerCase();
						usuario.setNombre(nombre);
						return usuario; 
					}); // Realiza la transformacion del stream
						
						
		
		
		nombres.subscribe(
					usuario -> Log.info(usuario.toString()), //Funcion para cada elemento que llega en la susccripción 
					error -> Log.error(error.getMessage()), // Funcion en caso de error
					new Runnable() { // Evento onComplete, corresponde a la función a ejecutar toda vez que termine el Stream
						@Override
						public void run() {
							Log.info("Ha finalizado la ejecución del observable con éxito!");
						}
					}
				);
		
		
	}

}
