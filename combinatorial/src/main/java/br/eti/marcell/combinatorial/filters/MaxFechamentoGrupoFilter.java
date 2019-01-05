package br.eti.marcell.combinatorial.filters;

import static br.eti.marcell.combinatorial.CombinacaoUtils.intersectionCount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Stopwatch;

public class MaxFechamentoGrupoFilter implements GrupoFilter {
	private int g;
	private Integer delta = null;
	private AtomicBoolean found = null;
	private Stack<Integer> lastFound = new Stack<>();

	public MaxFechamentoGrupoFilter(int g) {
		this(g, null, false);
	}

	public MaxFechamentoGrupoFilter(int g, boolean found) {
		this(g, null, found);
	}

	public MaxFechamentoGrupoFilter(int g, Integer delta) {
		this(g, delta, false);
	}

	public MaxFechamentoGrupoFilter(int g, Integer delta, boolean found) {
		this.g = g;
		this.delta = delta;
		if (found) {
			this.found = new AtomicBoolean(false);
		}
	}

	@Override
	public List<int[]> filter(List<int[]> cartelas) {
		Stopwatch innerWatch = Stopwatch.createStarted();
		Map<int[], AtomicLong> cartelaCounter = cartelas.stream().collect(
				Collectors.toMap(
						Function.identity(), 
						cartela -> new AtomicLong()
				)
		);
		
		AtomicInteger counter = new AtomicInteger();
		AtomicInteger percentual = new AtomicInteger();
		IntStream.range(0, cartelas.size()).parallel().boxed().forEach(i -> {
			if (found == null || !found.get()) {
				int[] cartela = cartelas.get(i);
				cartelas.parallelStream().skip(i).filter(inner -> intersectionCount(cartela, inner) >= g).forEach(inner -> {
					cartelaCounter.get(cartela).incrementAndGet();
					cartelaCounter.get(inner).incrementAndGet();
				});

				if (delta != null) {
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
				}

				if (found != null && !lastFound.empty() && cartelaCounter.get(cartela).get() >= lastFound.peek()) {
					found.set(true);
					System.out.println("Skipping");
				}
			}
		});
		int[] candidateMax = cartelas.parallelStream().max((c1, c2) -> Integer.compare(cartelaCounter.get(c1).intValue(), cartelaCounter.get(c2).intValue())).get();
		List<int[]> candidates = cartelaCounter.entrySet().parallelStream().filter(e -> e.getValue().get() == cartelaCounter.get(candidateMax).get()).map(e -> e.getKey()).collect(Collectors.toList());
		System.out.println("Feito - " + innerWatch.stop());
		return candidates;
	}

	@Override
	public List<int[]> clean(List<int[]> currentAvaliable, List<int[]> choosens) {
		Set<int[]> result =	choosens.stream().flatMap(choosen ->
				currentAvaliable.parallelStream().filter(inner -> intersectionCount(choosen, inner) >= g).collect(Collectors.toList()).stream()
		).collect(Collectors.toSet());
		return new ArrayList<>(result);
	}

}
