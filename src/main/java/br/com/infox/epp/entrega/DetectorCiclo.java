package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DetectorCiclo<T> {
	private RelacionamentoSource<T> source;

	public interface RelacionamentoSource<T> {
		public Collection<T> getPais(T item);

		public Collection<T> getFilhos(T item);
	}

	protected interface ItemSource<T> {
		public Collection<T> getItens(T item);
	}

	protected class PaiSource implements ItemSource<T> {
		@Override
		public Collection<T> getItens(T item) {
			return source.getPais(item);
		}
	}

	protected class FilhoSource implements ItemSource<T> {
		@Override
		public Collection<T> getItens(T item) {
			return source.getFilhos(item);
		}
	}

	protected static class Ciclo<T> {
		
		public static interface ToStringDelegate<T> {
			public String toString(T item);
		}
		
		private List<T> itens = new ArrayList<>();

		public Ciclo() {
		}

		public void add(T item) {
			itens.add(item);
		}

		public List<T> getItens() {
			return Collections.unmodifiableList(itens);
		}
		
		public String toString(ToStringDelegate<T> delegate) {
			Iterator<T> it = itens.iterator();
			String retorno = "";
			while(it.hasNext()) {
				T item = it.next();
				retorno += delegate.toString(item);
				if(it.hasNext()) {
					retorno += " -> ";
				}
			}
			return retorno;
		}
	}

	private Collection<T> getRelacionamentos(T item, ItemSource<T> itemSource, Set<T> itensVisitados) {
		Collection<T> retorno = new ArrayList<>();
		for (T itemRelacionado : itemSource.getItens(item)) {
			if (!itensVisitados.contains(itemRelacionado)) {
				retorno.add(itemRelacionado);
				itensVisitados.add(itemRelacionado);
				retorno.addAll(getRelacionamentos(itemRelacionado, itemSource, itensVisitados));
			}
		}
		return retorno;
	}

	private Collection<T> getRelacionamentos(T item, ItemSource<T> itemSource) {
		Set<T> itensVisitados = new HashSet<>();
		return getRelacionamentos(item, itemSource, itensVisitados);
	}

	public DetectorCiclo(RelacionamentoSource<T> source) {
		this.source = source;
	}

	protected Collection<T> getPaisRecursivamente(T itemFilho) {
		return getRelacionamentos(itemFilho, new PaiSource());
	}

	protected Collection<T> getFilhosRecursivamente(T itemPai) {
		return getRelacionamentos(itemPai, new FilhoSource());
	}

	private T getItemCiclo(Collection<T> itens, T itemProcurado, Set<T> itensVisitados) {
		if (itens.contains(itemProcurado)) {
			return itemProcurado;
		}
		for (T item : itens) {
			if (itensVisitados.contains(item)) {
				continue;
			}
			itensVisitados.add(item);
			Collection<T> arvoreFilhos = getFilhosRecursivamente(item);
			if (arvoreFilhos.contains(itemProcurado)) {
				return item;
			}
		}
		return null;
	}

	public Ciclo<T> getCiclo(T itemPai, T itemFilho) {
		Collection<T> arvoreFilhosFilho = new ArrayList<>();
		arvoreFilhosFilho.add(itemFilho);
		arvoreFilhosFilho.addAll(getFilhosRecursivamente(itemFilho));

		if (!arvoreFilhosFilho.contains(itemPai)) {
			return null;
		}

		Ciclo<T> ciclo = new Ciclo<>();
		ciclo.add(itemPai);
		ciclo.add(itemFilho);

		T itemAtual = itemFilho;
		Set<T> itensVisitados = new HashSet<>();
		do {
			itemAtual = getItemCiclo(source.getFilhos(itemAtual), itemPai, itensVisitados);
			if(itemAtual == null) {
				throw new RuntimeException("Erro ao localizar ciclo");
			}
			ciclo.add(itemAtual);
		} while (!itemAtual.equals(itemPai));

		return ciclo;
	}

}
