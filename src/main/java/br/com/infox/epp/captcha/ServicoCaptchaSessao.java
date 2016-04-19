package br.com.infox.epp.captcha;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

/**
 * @author paulo
 * Classe responsável por definir se um captcha deve ou ser apresentado ou não ao abrir uma tela 
 */
@SessionScoped
public class ServicoCaptchaSessao implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Define o número de vezes que uma tela pode ser mostrada para cada captcha resolvido
	 */
	private static final int FATOR_CAPTCHA = 5;
	
	/**
	 * Número de captchas resolvidos com sucesso
	 */
	private int captchasResolvidos = 0;
	/**
	 * Número de vezes em que uma tela a ser protegida com o captcha foi mostrada
	 */
	private int telasMostradas = 0;
	
	/**
	 * Deve ser chamada sempre que um captcha seja resolvido com sucesso
	 */
	public synchronized void captchaResolvido() {
		captchasResolvidos++;
	}
	
	/**
	 * Função que deve ser chamada cada vez que um recurso a ser protegido por captcha é mostrado
	 * @return Retorna true caso o captcha deva ser mostrado ao mostrar a tela
	 */
	public synchronized boolean telaMostrada() {
		telasMostradas++;
		return isMostrarCaptcha();
	}
	
	/**
	 * Diz se o captcha deve ser mostrado ou não
	 * @return
	 */
	public synchronized boolean isMostrarCaptcha() {
		if(captchasResolvidos <= 0) {
			return true;
		}
		return ((double)telasMostradas / captchasResolvidos > FATOR_CAPTCHA );
	}
}
