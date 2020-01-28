package br.com.infox.epp.loglab.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class EmpresaVO {

	private Long id;
    private Integer idPessoaJuridica;
    private String cnpj;
	private String tipoEmpresa;
	private String razaoSocial;
	private String nomeFantasia;
	private Date dataAbertura;	
	private String TelefoneCelular;
    private String telefoneFixo;
    private String email;
    private String codEstado;
    private String cidade;
    private String logradouro;
    private String bairro;
    private String complemento;
    private String numero;
    private String cep;
}
