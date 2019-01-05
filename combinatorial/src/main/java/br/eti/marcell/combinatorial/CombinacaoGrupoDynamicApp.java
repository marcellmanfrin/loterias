package br.eti.marcell.combinatorial;

import static br.eti.marcell.combinatorial.CombinacaoUtils.combina;
import static br.eti.marcell.combinatorial.CombinacaoUtils.intersectionCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Stopwatch;

public class CombinacaoGrupoDynamicApp {

	public static void main(String[] args) {
		Stopwatch watch = Stopwatch.createStarted();
		int n = 20;
		int c = 8;
		int g = 4;

		Set<Integer> items = IntStream.rangeClosed(1, n).boxed().collect(Collectors.toSet());
		List<int[]> combinacoes = convert(combina(items, g).collect(Collectors.toSet())); 
		List<int[]> cartelas = convert(combina(items, c).collect(Collectors.toSet()));
		List<int[]> choosens = new LinkedList<>();

		System.out.println("Iniciando - " + cartelas.size());
		boolean processed = false;
		do {
			Stopwatch innerWatch = Stopwatch.createStarted();
			Map<int[], AtomicLong> cartelaCounter = cartelas.stream().collect(
					Collectors.toMap(
							Function.identity(), 
							cartela -> new AtomicLong()
					)
			);
			int[] choosen;
			if (choosens.isEmpty()) {
				choosen = cartelas.get(0);
			} else {
				if (!processed) {
					AtomicInteger counter = new AtomicInteger();
					AtomicInteger percentual = new AtomicInteger();
					IntStream.range(0, cartelas.size()).parallel().boxed().forEach(i -> {
						int[] cartela = cartelas.get(i);
						cartelas.parallelStream().skip(i).filter(inner -> intersectionCount(cartela, inner) >= g).forEach(inner -> {
							cartelaCounter.get(cartela).incrementAndGet();
							cartelaCounter.get(inner).incrementAndGet();
						});
						final int delta = 20;
						int current = counter.incrementAndGet();
						int divide = (cartelas.size() / (100 / delta));
						if (divide != 0 && current % divide == 0) {
							int p = percentual.addAndGet(delta);
							long s = innerWatch.elapsed().getSeconds();
							long se = s*100/p;
							System.out.printf("%d%% (%02d:%02d; %02d:%02d) - ", p, s/60, s%60, se/60, se%60);
							if (current / (cartelas.size() / (100 / delta)) % 10 == 0) {
								System.out.println();
							}
						}
					});
					processed = true;
				}
				int[] candidateMax = cartelas.parallelStream().max((c1, c2) -> Integer.compare(cartelaCounter.get(c1).intValue(), cartelaCounter.get(c2).intValue())).get();
				List<int[]> candidates = cartelaCounter.entrySet().parallelStream().filter(e -> e.getValue().get() == cartelaCounter.get(candidateMax).get()).map(e -> e.getKey()).collect(Collectors.toList());
				Map<Integer, List<int[]>> counters = candidates.stream().collect(Collectors.groupingBy(candidate ->
					(int) combinacoes.stream().filter(combinacao -> Arrays.asList(candidate).containsAll(Arrays.asList(combinacao))).count()
				));
				choosen = counters.get(counters.keySet().stream().mapToInt(v -> v).max().getAsInt()).get(0);
				System.out.println();
			}
			Set<int[]> replaced = cartelas.parallelStream().filter(inner -> intersectionCount(choosen, inner) >= g).collect(Collectors.toSet());
			replaced.parallelStream().forEach(r -> cartelaCounter.remove(r));
			cartelas.removeAll(replaced);
			if (processed) {
				AtomicInteger counter = new AtomicInteger();
				AtomicInteger percentual = new AtomicInteger();
				IntStream.range(0, cartelas.size()).parallel().boxed().forEach(i -> {
					int[] cartela = cartelas.get(i);
					replaced.parallelStream().filter(inner -> intersectionCount(cartela, inner) >= g).forEach(inner -> {
						cartelaCounter.get(cartela).decrementAndGet();
					});
					final int delta = 20;
					int current = counter.incrementAndGet();
					int divide = (cartelas.size() / (100 / delta));
					if (divide != 0 && current % divide == 0) {
						int p = percentual.addAndGet(delta);
						long s = innerWatch.elapsed().getSeconds();
						long se = s*100/p;
						System.out.printf("%d%% (%02d:%02d; %02d:%02d) - ", p, s/60, s%60, se/60, se%60);
						if (current / (cartelas.size() / (100 / delta)) % 10 == 0) {
							System.out.println();
						}
					}
				});
			}
			choosens.add(choosen);
			combinacoes.removeAll(combinacoes.stream().filter(combinacao -> Arrays.asList(choosen).containsAll(Arrays.asList(combinacao))).collect(Collectors.toList()));
			System.out.println(replaced.size() + " - " + Arrays.toString(choosen));
			System.out.println("Feito - " + innerWatch.stop());
		} while (cartelas.size() > 0);

		System.out.println(choosens.size());
		System.out.println("Terminou - " + watch.stop());
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
