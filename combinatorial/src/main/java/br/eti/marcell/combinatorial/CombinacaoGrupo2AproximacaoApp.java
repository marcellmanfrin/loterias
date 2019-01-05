package br.eti.marcell.combinatorial;

import static br.eti.marcell.combinatorial.CombinacaoUtils.combina;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import br.eti.marcell.combinatorial.filters.GrupoFilter;
import br.eti.marcell.combinatorial.filters.PrimeiraCartelaFilter;
import br.eti.marcell.combinatorial.filters.TwoApproximationFilter;

public class CombinacaoGrupo2AproximacaoApp {

	public static void main(String[] args) {
		Stopwatch watch = Stopwatch.createStarted();
		int n = 19;
		int c = 7;
		int g = 4;
		final boolean lerPrevious = false;

		Set<Integer> items = IntStream.rangeClosed(1, n).boxed().collect(Collectors.toSet());
		List<int[]> cartelas = convert(combina(items, c).collect(Collectors.toSet()));
		List<int[]> choosens = new LinkedList<>();

		List<GrupoFilter> filtros = new LinkedList<>();
		filtros.add(new PrimeiraCartelaFilter());
		filtros.add(new TwoApproximationFilter(g));

		if (lerPrevious) {
			List<int[]> previous = CombinacaoUtils.readFromFile("" + n + "." + c + "." + g + "." + "clean.txt");
			System.out.println("Previous: " + previous.size());
			previous.stream()
				.peek(p -> System.out.println(Arrays.toString(p)))
				.forEach(p -> cleanChoosed(cartelas, filtros, Lists.newArrayList(p), g));
			choosens.addAll(previous);
		}

		while (!cartelas.isEmpty()) {
			Stopwatch innerWatch = Stopwatch.createStarted();
			System.out.println("Iniciando - " + cartelas.size());
			List<int[]> currentChoosens;
			List<int[]> candidates = cartelas;

			System.out.println("Possíveis antes de filtros: " + candidates.size());
			for (GrupoFilter f : filtros) {
				candidates = f.filter(candidates);
			}
			System.out.println("Possíveis pós-filtros: " + candidates.size());
			System.out.println(CombinacaoUtils.toString(candidates));
			currentChoosens = candidates;

			Set<int[]> replaced = cleanChoosed(cartelas, filtros, currentChoosens, g);
			System.out.println(replaced.size() + " - " + CombinacaoUtils.toString(currentChoosens));

			choosens.addAll(currentChoosens);
			System.out.println("Feito - " + innerWatch.stop());
		}

		System.out.println(choosens.size());
		System.out.println("Terminou - " + watch.stop());
	}

	private static Set<int[]> cleanChoosed(List<int[]> cartelas, List<GrupoFilter> filtros, List<int[]> currentChoosens, int g) {
		Set<int[]> replaced = new HashSet<>();
		filtros.stream().forEach(f -> replaced.addAll(f.clean(cartelas, currentChoosens)));
		cartelas.removeAll(replaced);
		return replaced;
	}

	private static List<int[]> convert(Collection<Collection<Integer>> values) {
		List<int[]> result = new ArrayList<>(values.size());
		for (Collection<Integer> set : values) {
			int[] item = new int[set.size()];
			int j = 0;
			for (Integer v : set) {
				item[j++] = v;
			}
			result.add(item);
		}
		return result;
	}

}
