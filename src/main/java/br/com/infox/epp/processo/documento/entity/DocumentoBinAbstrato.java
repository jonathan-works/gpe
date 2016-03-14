package br.com.infox.epp.processo.documento.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.jboss.seam.util.Strings;

import br.com.infox.constants.LengthConstants;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.entity.RegistroAssinaturaSuficiente;
import br.com.infox.epp.processo.documento.query.DocumentoBinQuery;
import br.com.infox.hibernate.UUIDGenericType;

public abstract class DocumentoBinAbstrato implements Serializable {

	
	
}
