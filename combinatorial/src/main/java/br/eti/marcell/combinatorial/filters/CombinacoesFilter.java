package br.eti.marcell.combinatorial.filters;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.eti.marcell.combinatorial.CombinacaoUtils;

public class CombinacoesFilter implements GrupoFilter {
	private Collection<int[]> combinacoes;

	public CombinacoesFilter(Collection<int[]> combinacoes) {
		this.combinacoes = combinacoes;
	}

	/* (non-Javadoc)
	 * @see br.eti.marcell.combinatorial.combinatorial.GrupoFilter#filter(java.util.List)
	 */
	@Override
	public List<int[]> filter(List<int[]> candidates) {
		Map<Integer, List<int[]>> counters = candidates.stream().collect(Collectors.groupingBy(candidate ->
			(int) combinacoes.stream().filter(combinacao -> CombinacaoUtils.containsAll(candidate, combinacao)).count()
		));
		return counters.get(counters.keySet().stream().mapToInt(v -> v).max().getAsInt());
	}

	/* (non-Javadoc)
	 * @see br.eti.marcell.combinatorial.combinatorial.GrupoFilter#clean(int[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<int[]> clean(List<int[]> currentAvaliable, List<int[]> choosens) {
		choosens.stream().forEach(choosen ->
			combinacoes.removeAll(combinacoes.stream().filter(combinacao -> CombinacaoUtils.containsAll(choosen, combinacao)).collect(Collectors.toList()))
		);
		return Collections.EMPTY_LIST;
	}

}
