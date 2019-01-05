package br.eti.marcell.combinatorial;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.javagl.utils.math.combinatorics.ChoiceIterable;

public class CombinacaoUtils {

	@SuppressWarnings("unchecked")
	public static <K> Stream<List<K>> combina(Collection<K> items, int k) {
		return combina(Arrays.asList((K[]) items.toArray()), k);
	}

	public static <K> Stream<List<K>> combina(List<K> items, int k) {
		return StreamSupport.stream(new ChoiceIterable<>(k, items).spliterator(), false);
	}

	/**
	 * Calcula o primeiro contem todos os elementos do segundo array 
	 * @param c1 primeiro array
	 * @param c2 segundo array
	 * @return quantidade de numeros da intersecao entre os arrays
	 */
	public static <T> boolean containsAll(int[] c1, int[] c2) {
		boolean result = true;
		for (int i = 0; i < c2.length; i++) {
			boolean found = false;
			for (int j = 0; j < c1.length; j++) {
				if (c1[j] == c2[i]) {
					found = true;
					break;
				}
			}
			if (!found) {
				result = false;
				break;
			}
		}
		return result;
	}

	public static String toString(List<int[]> list) {
		return "[" + list.stream().map(i -> Arrays.toString(i)).collect(Collectors.joining(", ")) + "]";
	}

	public static String toString(Map<Integer, List<int[]>> map) {
		return "[" + map.entrySet().stream().map(e -> e.getKey().toString() + "=" + e.getValue().size()).collect(Collectors.joining(", ")) + "]";
	}

	/**
	 * Calcula a quantidade numeros que sao comuns entre os dois arrays 
	 * @param c1 primeiro array
	 * @param c2 segundo array
	 * @return quantidade de numeros da intersecao entre os arrays
	 */
	public static int intersectionCount(int[] c1, int[] c2) {
		int result = 0;
		for (int i = 0; i < c1.length; i++) {
			for (int j = 0; j < c2.length; j++) {
				if (c1[i] == c2[j]) {
					result++;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Calcula a quantidade numeros que sao comuns entre os dois arrays 
	 * @param c1 primeiro array
	 * @param c2 segundo array
	 * @return quantidade de numeros da intersecao entre os arrays
	 */
	public static int intersectionCount(byte[] c1, byte[] c2) {
		int result = 0;
		for (int i = 0; i < c1.length; i++) {
			for (int j = 0; j < c2.length; j++) {
				if (c1[i] == c2[j]) {
					result++;
					break;
				}
			}
		}
		return result;
	}

	public static List<int[]> readFromFile(String file) {
		System.out.println(file);
		List<int[]> result = new LinkedList<>();
		ClassLoader classLoader = CombinacaoUtils.class.getClassLoader();
		try (Scanner scanner = new Scanner(new File(classLoader.getResource(file).getFile()))) {
			while (scanner.hasNext()) {
				result.add(Arrays.stream(scanner.nextLine().split("[,\\s]+")).mapToInt(s -> Integer.parseInt(s)).toArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
