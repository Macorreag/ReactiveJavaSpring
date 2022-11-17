package com.bolsadeideas.springboot.reactor.app;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner{

	private static final Logger Log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	public void run(String... args) throws Exception {
		//ejemploIterable();
		ejemploFlatMap();
		
	}
	
	public void ejemploFlatMap() throws Exception {
		// TODO Auto-generated method stub
		
		
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Pedro Fulan");
		usuariosList.add("Andres Guzman");
		usuariosList.add("Jhon Doe");
		usuariosList.add("Diego Sultano");
		usuariosList.add("Juan Medrano");
		usuariosList.add("Juan Mengano");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willies");
		
		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase())) // Realiza la transformacion del stream
				.flatMap(usuario -> {
					if(usuario.getNombre().equalsIgnoreCase("bruce")) {
						return Mono.just(usuario);
					}else {
						return Mono.empty();
					}
				})
				.map(usuario -> {
						String nombre = usuario.getNombre().toLowerCase();
						usuario.setNombre(nombre);
						return usuario; 
					})
				.subscribe(usuario -> Log.info(usuario.toString())
				);
		
		
	}

	public void ejemploIterable() throws Exception {
		// TODO Auto-generated method stub
		
		
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Pedro Fulan");
		usuariosList.add("Andres Guzman");
		usuariosList.add("Jhon Doe");
		usuariosList.add("Diego Sultano");
		usuariosList.add("Juan Medrano");
		usuariosList.add("Juan Mengano");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willies");
		
		Flux<String> nombres = /*Flux.just("Andres Guzman", "Pedro Fulan", "Jhon Doe", "Diego Sultano", "Juan Medrano", "Juan Mengano", "Bruce Lee", "Bruce Willies");*/
							Flux.fromIterable(usuariosList);
		
		
		
		Flux<Usuario> usuarios =	nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase())) // Realiza la transformacion del stream
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
						
						
		
		
		usuarios.subscribe(
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


