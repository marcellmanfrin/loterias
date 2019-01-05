package br.eti.marcell.combinatorial.filters;

import static br.eti.marcell.combinatorial.CombinacaoUtils.intersectionCount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class TwoApproximationFilter implements GrupoFilter {
	private int g;

	public TwoApproximationFilter(int g) {
		this.g = g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.eti.marcell.combinatorial.combinatorial.GrupoFilter#filter(java.util.List)
	 */
	@Override
	public List<int[]> filter(List<int[]> candidates) {
		if (candidates.isEmpty() || candidates.size() < 2) {
			return candidates;
		}
		int[] choosen1 = candidates.get(0);
		Optional<int[]> choosen2 = candidates.parallelStream().filter(inner -> intersectionCount(candidates.get(0), inner) >= g).findAny();
		if (choosen2.isPresent()) {
			return Lists.newArrayList(choosen1, choosen2.get());
		}
		return Lists.newArrayList(choosen1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.eti.marcell.combinatorial.combinatorial.GrupoFilter#clean(int[])
	 */
	@Override
	public List<int[]> clean(List<int[]> currentAvaliable, List<int[]> choosens) {
		Set<int[]> result = choosens.stream().flatMap(choosen -> 
			currentAvaliable.parallelStream().filter(inner -> intersectionCount(choosen, inner) >= g).collect(Collectors.toList()).stream()
		).collect(Collectors.toSet());
		return new ArrayList<>(result);
	}

}
