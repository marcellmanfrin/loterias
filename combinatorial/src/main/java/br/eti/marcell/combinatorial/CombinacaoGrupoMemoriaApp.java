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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Stopwatch;

public class CombinacaoGrupoMemoriaApp {

	public static void main(String[] args) {
		Stopwatch watch = Stopwatch.createStarted();
		int n = 19;
		int c = 7;
		int g = 4;

		Set<Integer> items = IntStream.rangeClosed(1, n).boxed().collect(Collectors.toSet());
		List<byte[]> cartelas = convert(combina(items, c).collect(Collectors.toSet()));
		List<byte[]> choosens = new LinkedList<>();

		do {
			Stopwatch innerWatch = Stopwatch.createStarted();
			System.out.println("Iniciando - " + cartelas.size());
			Map<byte[], Set<byte[]>> cartelaCartelas = cartelas.stream().collect(
					Collectors.toMap(
							Function.identity(), 
							cartela -> ConcurrentHashMap.newKeySet()
					)
			);
			byte[] choosen;
			if (choosens.isEmpty()) {
				AtomicInteger counter = new AtomicInteger();
				AtomicInteger percentual = new AtomicInteger();
				IntStream.range(0, cartelas.size()).parallel().boxed().forEach(i -> {
					byte[] cartela = cartelas.get(i);
					cartelas.parallelStream().skip(i).filter(inner -> intersectionCount(cartela, inner) >= g).forEach(inner -> {
						cartelaCartelas.get(cartela).add(inner);
						cartelaCartelas.get(inner).add(cartela);
					});
					final int delta = 1;
					int current = counter.incrementAndGet();
					if (current % (cartelas.size() / (100 / delta)) == 0) {
						int p = percentual.addAndGet(delta);
						long s = innerWatch.elapsed().getSeconds();
						long se = s*100/p;
						System.out.printf("%d%% (%02d:%02d; %02d:%02d) - ", p, s/60, s%60, se/60, se%60);
						if (current / (cartelas.size() / (100 / delta)) % 10 == 0) {
							System.out.println();
						}
					}
				});
				System.out.println();
				choosen = cartelas.get(0);
			} else {
				choosen = cartelas.parallelStream().max((c1, c2) -> Integer.compare(cartelaCartelas.get(c1).size(), cartelaCartelas.get(c2).size())).get();
			}
			Set<byte[]> replaced = cartelaCartelas.get(choosen);
			replaced.stream().forEach(i -> cartelaCartelas.remove(i));
			cartelaCartelas.values().stream().forEach(i -> i.removeAll(replaced));
			cartelas.removeAll(replaced);
			choosens.add(choosen);
			System.out.println(replaced.size() + " - " + Arrays.toString(choosen));
			System.out.println("Feito - " + innerWatch.stop());
		} while (cartelas.size() > 0);

		System.out.println(choosens.size());
		System.out.println("Terminou - " + watch.stop());
	}

	private static List<byte[]> convert(Collection<Collection<Integer>> values) {
		List<byte[]> result = new ArrayList<>(values.size());
		for (Collection<Integer> set : values) {
			byte[] item = new byte[set.size()];
			int j = 0;
			for (Integer v : set) {
				item[j++] = v.byteValue();
			}
			result.add(item);
		}
		return result;
	}

}
