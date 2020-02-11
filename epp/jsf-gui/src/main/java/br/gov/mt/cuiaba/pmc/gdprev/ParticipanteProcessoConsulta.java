package br.gov.mt.cuiaba.pmc.gdprev;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = ParticipanteProcesso.TABLE_NAME)
@Getter
@Immutable
@NoArgsConstructor
public class ParticipanteProcessoConsulta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participante_processo", insertable = false, updatable = false)
    private ParticipanteProcesso participanteProcesso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id_pessoa_fisica", insertable = false, updatable = false)
    private PessoaFisica pessoaFisica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id_pessoa_juridica", insertable = false, updatable = false)
    private PessoaJuridica pessoaJuridica;

}