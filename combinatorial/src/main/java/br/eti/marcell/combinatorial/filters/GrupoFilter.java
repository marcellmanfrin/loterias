package br.eti.marcell.combinatorial.filters;

import java.util.List;

public interface GrupoFilter {

	List<int[]> filter(List<int[]> candidates);

	List<int[]> clean(List<int[]> currentAvaliable, List<int[]> choosens);

}