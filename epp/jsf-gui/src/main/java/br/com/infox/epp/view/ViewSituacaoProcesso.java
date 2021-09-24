package br.com.infox.epp.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name = "vs_situacao_processo")
@EqualsAndHashCode(of="idTaskInstance")
public class ViewSituacaoProcesso implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_taskinstance", nullable = false, insertable = false, updatable = false)
    private Long idTaskInstance;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_taskinstance", nullable = false, insertable = false, updatable = false)
    private TaskInstance taskInstance;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processinstace", nullable = false, insertable = false, updatable = false)
    private ProcessInstance processInstance;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fluxo", nullable = false, insertable = false, updatable = false)
    private Fluxo fluxo;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_natureza_categoria_fluxo", nullable = false, insertable = false, updatable = false)
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_natureza_categoria_fluxo_root", nullable = false, insertable = false, updatable = false)
    private NaturezaCategoriaFluxo naturezaCategoriaFluxoRoot;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caixa", nullable = false, insertable = false, updatable = false)
    private Caixa caixa;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_cadastro_processo", nullable = false, insertable = false, updatable = false)
    private UsuarioLogin usuarioCadastro;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prioridade_processo", nullable = false, insertable = false, updatable = false)
    private PrioridadeProcesso prioridadeProcesso;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false, insertable = false, updatable = false)
    private Processo processo;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo_root", nullable = false, insertable = false, updatable = false)
    private Processo processoRoot;

    @Column(name = "nr_processo", nullable = false, insertable = false, updatable = false)
    private String numeroProcesso;

    @Column(name = "temdocumentoparaassinar", nullable = false, insertable = false, updatable = false)
    private Boolean numeroProcessoRoot;

    @Column(name = "in_documento_assinar", nullable = false, insertable = false, updatable = false)
    private Boolean temDocumentoParaAssinar;

}