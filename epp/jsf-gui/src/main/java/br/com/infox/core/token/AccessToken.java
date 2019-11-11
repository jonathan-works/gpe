package br.com.infox.core.token;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.com.infox.hibernate.UUIDGenericType;

@Entity
@Table(name = "tb_access_token")
public class AccessToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "AccessTokenGenerator", allocationSize = 1, initialValue = 1, sequenceName = "sq_access_token")
	@GeneratedValue(generator = "AccessTokenGenerator", strategy = GenerationType.SEQUENCE)
	@Column(name = "id_access_token")
	private Long id;
	
	@NotNull
	@Type(type = UUIDGenericType.TYPE_NAME)
	@Column(name = "ds_token", nullable = false, unique = true)
	private UUID token;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tp_token_requester", nullable = false)
	private TokenRequester tokenRequester;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getToken() {
		return token;
	}

	public void setToken(UUID token) {
		this.token = token;
	}

	public TokenRequester getTokenRequester() {
		return tokenRequester;
	}

	public void setTokenRequester(TokenRequester tokenRequester) {
		this.tokenRequester = tokenRequester;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AccessToken))
			return false;
		AccessToken other = (AccessToken) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
