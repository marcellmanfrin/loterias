package br.eti.marcell.combinatorial.filters;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class PrimeiraCartelaFilter implements GrupoFilter {
	private boolean choosen = false;

	/* (non-Javadoc)
	 * @see br.eti.marcell.combinatorial.combinatorial.GrupoFilter#filter(java.util.List)
	 */
	@Override
	public List<int[]> filter(List<int[]> candidates) {
		if (candidates.isEmpty() || choosen) {
			return candidates;
		}
		return Lists.newArrayList(candidates.get(0));
	}

	/* (non-Javadoc)
	 * @see br.eti.marcell.combinatorial.combinatorial.GrupoFilter#clean(int[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<int[]> clean(List<int[]> currentAvaliable, List<int[]> choosens) {
		choosen = true;
		return Collections.EMPTY_LIST;
	}

}
