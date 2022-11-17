package com.bolsadeideas.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Comentarios;
import com.bolsadeideas.springboot.reactor.app.models.Usuario;
import com.bolsadeideas.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner{

	private static final Logger Log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//ejemploIterable();
//		ejemploFlatMap();
//		ejemploToString();
//		ejemploCollectList();
//		ejemploUsuarioComentariosFlatMap();
//		ejemploUsuarioComentariosZipWith();
//		ejemploUsuarioComentariosZipWithForma2();
//		ejemploZipWithrangos();
		
//		ejemploDelayElements();
//		ejemploIntervaloInfinito();
		ejemploIntervaloDesdeCreate();
		
		
		
	}

	public void ejemploIntervaloDesdeCreate() {
		Flux.create(emitter -> {
			Timer timer = new Timer();
			
			timer.schedule(new TimerTask() 
					{
				
						private Integer contador = 0;
				
						@Override
						public void run() {
							emitter.next(++contador);
							
								if(contador == 10) {
									timer.cancel();
									emitter.complete();
								}
								if(contador == 5) {
									
									timer.cancel();
									emitter.error(new InterruptedException("Error, se ha detenido el Flux en 5"));
									
								}
							}
							
						}
								
				
					, 1000, 1000);
		})
		.subscribe(next -> Log.info(next.toString()),
				error -> Log.error(error.getMessage()),
				()-> Log.info("Hemos Terminado")
				
				)
		
		;
	}
	
	
	public void ejemploIntervaloInfinito() throws InterruptedException  {
		
		CountDownLatch latch = new CountDownLatch(1);
		
		
		Flux.interval(Duration.ofSeconds(1))
		.doOnTerminate(latch::countDown)
		.flatMap(i -> {
			if(i >= 5) {
				return Flux.error(new InterruptedException("Solo hasta 5!"));
			}
			return Flux.just(i);
		})
		.map(i -> "Hola " + i)
		.retry(2)
		.subscribe(s -> Log.info(s), 
					e -> Log.error(e.getMessage())
				);
		
		latch.await();
		
	}
	
	public void ejemploDelayElements() throws InterruptedException {
		Flux<Integer> rango = 	Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> Log.info(i.toString()));
		rango.subscribe(); // Esto se ejecuta en segundo plano para no bloquear la ejecución
		
		Thread.sleep(13000); //Se deja el hilo principal esperando 13 segundos para poder ver las impresiones del subproceso

	}
	
	public void ejemploInterval() {
		Flux<Integer> rangos = 	Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		
		rangos.zipWith(retraso, (ra, re) -> ra)
		.doOnNext(i -> Log.info(i.toString()))
		.blockLast(); // Bloquear el proceso hasta el ultimo elementos
	}
	
	
	public void ejemploZipWithrangos() {
		
		Flux<Integer> rangos = Flux.range(0, 4);
		
		Flux.just(1,2,3,4)
		.map(i  -> i*2)
		.zipWith(rangos, (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux %d", uno, dos))
		.subscribe(texto -> Log.info(texto));
		
	}
	
//	public Usuario crearUsuario() {
//		return new Usuario("Jhon", "Doe");
//	}
//	

	public void ejemploUsuarioComentariosZipWithForma2() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> {
			return new Usuario("Jhon", "Doe");
		});
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola-pepe, que tal!");
			comentarios.addComentarios("Mañana voy a la plata!");
			comentarios.addComentarios("Estioy tomando el curso de spring!");
			return comentarios;
		});
	
		
		Mono<UsuarioComentarios> usuarioConComentarios = 
				usuarioMono.zipWith(comentariosUsuarioMono)
				.map(tuple -> {
					Usuario u = tuple.getT1();
					Comentarios c = tuple.getT2();
					return new UsuarioComentarios(u, c);
				});
				
		usuarioConComentarios.subscribe(uc -> Log.info(uc.toString()));
		
	}
	
	public void ejemploUsuarioComentariosZipWith() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> {
			return new Usuario("Jhon", "Doe");
		});
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola-pepe, que tal!");
			comentarios.addComentarios("Mañana voy a la plata!");
			comentarios.addComentarios("Estioy tomando el curso de spring!");
			return comentarios;
		});
	
		
		Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono.zipWith(comentariosUsuarioMono, (usuario, comentariosUsuario) -> new UsuarioComentarios(usuario, comentariosUsuario));
				
		usuarioConComentarios.subscribe(uc -> Log.info(uc.toString()));
		
	}
	
	public void ejemploUsuarioComentariosFlatMap() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> {
			return new Usuario("Jhon", "Doe");
		});
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola-pepe, que tal!");
			comentarios.addComentarios("Mañana voy a la plata!");
			comentarios.addComentarios("Estioy tomando el curso de spring!");
			return comentarios;
		});
	
		
		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentarios(u, c)) )
			.subscribe(uc -> Log.info(uc.toString()));
		
	}
	
	
	public void ejemploCollectList() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Pedro", "Fulan"));
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Jhon", "Doe"));
		usuariosList.add(new Usuario("Diego", "Sultano"));
		usuariosList.add(new Usuario("Juan", "Medrano"));
		usuariosList.add(new Usuario("Juan", "Mengano"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willies"));
		
		Flux.fromIterable(usuariosList)
		.collectList()
		.subscribe(lista -> {
			lista.forEach( item -> Log.info(item.toString()));
		});
	}
	
	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Pedro", "Fulan"));
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Jhon", "Doe"));
		usuariosList.add(new Usuario("Diego", "Sultano"));
		usuariosList.add(new Usuario("Juan", "Medrano"));
		usuariosList.add(new Usuario("Juan", "Mengano"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willies"));
		
		Flux.fromIterable(usuariosList)
				.map(usuario -> usuario.getNombre().toUpperCase().concat(usuario.getApellido().toUpperCase())) // Realiza la transformacion del stream en Flujo de String
				.flatMap(nombre -> {
					if(nombre.contains("bruce".toUpperCase())) {
						return Mono.just(nombre);
					}else {
						return Mono.empty();
					}
				})
				.map(nombre -> {
						return nombre.toLowerCase(); 
					})
				.subscribe(usuario -> Log.info(usuario.toString())
				);
		
		
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


