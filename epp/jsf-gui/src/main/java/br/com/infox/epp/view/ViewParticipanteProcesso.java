package br.com.infox.epp.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.jsf.converter.CnpjConverter;
import br.com.infox.jsf.converter.CpfConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name = "vs_participante_processo")
@EqualsAndHashCode(of="idParticipanteProcesso")
public class ViewParticipanteProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_participante_processo", nullable = false, insertable = false, updatable = false)
    private Long idParticipanteProcesso;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participante_processo", nullable = false, insertable = false, updatable = false)
    private ParticipanteProcesso participanteProcesso;

    @Getter @Setter
    @Column(name = "ds_caminho_absoluto", nullable = false, insertable = false, updatable = false)
    private String caminhoAbsoluto;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false, insertable = false, updatable = false)
    private Processo processo;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_parte", nullable = false, insertable = false, updatable = false)
    private TipoParte tipoParte;

    @Getter @Setter
    @Column(name = "nr_cpf_cnpj", nullable = false, insertable = false, updatable = false)
    private String cpfCnpj;

    @Getter @Setter
    @Column(name = "nm_pessoa", nullable = false, insertable = false, updatable = false)
    private String nomePessoa;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_pessoa", nullable = false, insertable = false, updatable = false)
    private TipoPessoaEnum tipoPessoa;

    @Transient
    public String getCodigoFormatado() {
        if (TipoPessoaEnum.F.equals(tipoPessoa)) {
            return CpfConverter.format(getCpfCnpj());
        } else if (TipoPessoaEnum.J.equals(tipoPessoa)) {
            return CnpjConverter.format(getCpfCnpj());
        }
        return getCpfCnpj();
    }

}