package org.iesalandalus.programacion.tutorias.mvc.modelo.negocio.ficheros;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import org.iesalandalus.programacion.tutorias.mvc.modelo.dominio.Alumno;
import org.iesalandalus.programacion.tutorias.mvc.modelo.negocio.IAlumnos;

public class Alumnos implements IAlumnos {

	private static final String NOMBRE_FICHERO_ALUMNOS = "datos/alumnos.dat";
	private List<Alumno> coleccionAlumnos;

	// Constructor
	public Alumnos() {
		coleccionAlumnos = new ArrayList<Alumno>();
	}

	@Override
	public void comenzar() {
		int numeroExpedienteAlumno = 0;
		File ficheroAlumnos = new File(NOMBRE_FICHERO_ALUMNOS);
		try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ficheroAlumnos))) {
			Alumno alumno = null;
			do {
				alumno = (Alumno) entrada.readObject();
				insertar(alumno);
				numeroExpedienteAlumno = identificadorAlumno(alumno);
			} while (alumno != null);

		} catch (ClassNotFoundException e) {
			System.out.println("No puedo encontrar la clase que tengo que leer.");
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: No puedo abrir el fichero de alumnos.");
		} catch (EOFException e) {
			Alumno.comprobarUltimoIdentificador(numeroExpedienteAlumno);
			System.out.println("Fichero alumnos leído satisfactoriamente.");
		} catch (IOException e) {
			System.out.println("Error inesperado de Entrada/Salida.");

		} catch (OperationNotSupportedException e) {
			System.out.println(e.getMessage());
		}

	}

	public int identificadorAlumno(Alumno alumno) {
		int expedienteAnterior = 0;
		String[] ExpedienteAlumno = alumno.getExpediente().split("_");
		String numeroExpediente = ExpedienteAlumno[2];
		int expediente = Integer.parseInt(numeroExpediente);
		if (expediente > expedienteAnterior) {
			expedienteAnterior = expediente;
		}
		return expedienteAnterior;
	}

	@Override
	public void terminar() {
		File ficheroAlumnos = new File(NOMBRE_FICHERO_ALUMNOS);
		try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ficheroAlumnos))) {
			for (Alumno alumno : coleccionAlumnos) {
				salida.writeObject(alumno);
			}
			System.out.println("Fichero alumnos escrito satisfactoriamente.");
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: No puedo crear el fichero de alumnos.");
		} catch (IOException e) {
			System.out.println("Error inesperado de Entrada/Salida.");
		}
	}

	// Getters
	@Override
	public List<Alumno> get() {
		List<Alumno> alumnosOrdenados = copiaProfundaAlumnos();
		alumnosOrdenados.sort(Comparator.comparing(Alumno::getCorreo));
		return alumnosOrdenados;
	}

	// Copia profunda alumnos
	private List<Alumno> copiaProfundaAlumnos() {
		List<Alumno> copiaAlumno = new ArrayList<>();
		for (Alumno alumno : coleccionAlumnos) {
			copiaAlumno.add(new Alumno(alumno));
		}
		return copiaAlumno;
	}

	@Override
	public int getTamano() {
		return coleccionAlumnos.size();
	}

	// Insertar alumno
	@Override
	public void insertar(Alumno alumno) throws OperationNotSupportedException {
		if (alumno == null) {
			throw new NullPointerException("ERROR: No se puede insertar un alumno nulo.");
		}

		int indice = coleccionAlumnos.indexOf(alumno);
		if (indice == -1) {
			coleccionAlumnos.add(new Alumno(alumno));
		} else {
			throw new OperationNotSupportedException("ERROR: Ya existe un alumno con ese expediente.");
		}

	}

	// Buscar alumnos
	@Override
	public Alumno buscar(Alumno alumno) {
		if (alumno == null) {
			throw new IllegalArgumentException("ERROR: No se puede buscar un alumno nulo.");
		}

		int indice = coleccionAlumnos.indexOf(alumno);
		if (indice == -1) {
			return null;
		} else {
			return new Alumno(coleccionAlumnos.get(indice));
		}
	}

	// Borrar alumnos
	@Override
	public void borrar(Alumno alumno) throws OperationNotSupportedException {
		if (alumno == null) {
			throw new IllegalArgumentException("ERROR: No se puede borrar un alumno nulo.");
		}

		int indice = coleccionAlumnos.indexOf(alumno);
		if (indice == -1) {
			throw new OperationNotSupportedException("ERROR: No existe ningún alumno con ese expediente.");
		} else {
			coleccionAlumnos.remove(indice);
		}
	}

}
