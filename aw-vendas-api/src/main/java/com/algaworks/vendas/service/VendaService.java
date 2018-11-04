package com.algaworks.vendas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algaworks.vendas.model.Venda;
import com.algaworks.vendas.repository.Produtos;
import com.algaworks.vendas.repository.Vendas;

@Service
public class VendaService {
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Produtos produtos;
	
	public Venda adicionar(Venda venda) {
		venda.setCadastro(LocalDateTime.now());
		//setar em todos os itens a venda que ela esta sendo relacionada e setar tambem e cada item qual produto;
		venda.getItens().forEach(i -> {
			i.setVenda(venda);
			i.setProduto(produtos.findById(i.getProduto().getId()).get());
		});
		
		// passar em todos os itens, verificando o valor do que esta no Produto e multiplicar pela quantidade
		// depois ele soma tudo isso (reduce) - accumulaçao
		BigDecimal totalItens = venda.getItens().stream()
				.map(i -> i.getProduto().getValor().multiply(new BigDecimal(i.getQuantidade())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		// soma com o valor do frete
		venda.setTotal(totalItens.add(venda.getFrete()));
		
		return vendas.save(venda);
	}
}
