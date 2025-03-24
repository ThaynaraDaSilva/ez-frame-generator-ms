package br.duosilva.tech.solutions.ez.frame.generator.ms.domain.model;

/**
 * 
 * <ul>
 *   <li><b>PENDING</b>: Vídeo enviado para fila, aguardando início do processamento</li>
 *   <li><b>PROCESSING</b>: Processamento em andamento</li>
 *   <li><b>COMPLETED</b>: Processamento finalizado com sucesso, arquivo .zip gerado</li>
 *   <li><b>FAILED</b>: Ocorreu erro no processamento, notificação será enviada ao usuário</li>
 * </ul>
 * 
 * */
public enum ProcessingStatus {

	PENDING, PROCESSING, COMPLETED, FAILED

}
