package br.eti.marcell.combinatorial.filters;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class Seleciona1Filter implements GrupoFilter {

	/* (non-Javadoc)
	 * @see br.eti.marcell.combinatorial.combinatorial.GrupoFilter#filter(java.util.List)
	 */
	@Override
	public List<int[]> filter(List<int[]> candidates) {
		if (candidates.isEmpty()) {
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
		return Collections.EMPTY_LIST;
	}

}
