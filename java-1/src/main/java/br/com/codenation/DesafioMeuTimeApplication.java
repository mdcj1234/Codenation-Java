package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;

public class DesafioMeuTimeApplication implements MeuTimeInterface {

	private final Map<Long, Time> times = new TreeMap<>();
	private final Map<Long, Jogador> jogadores = new TreeMap<>();

	@Desafio("incluirTime")
	public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal, String corUniformeSecundario) {
		if (times.containsKey(id))  throw new IdentificadorUtilizadoException();

		times.put(id,new Time(nome,dataCriacao,corUniformePrincipal,corUniformeSecundario));
	}

	@Desafio("incluirJogador")
	public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade, BigDecimal salario) {
		if (jogadores.containsKey(id)) throw new IdentificadorUtilizadoException();
		if (!times.containsKey(idTime)) throw new TimeNaoEncontradoException();

		jogadores.put(id, new Jogador(idTime,nome,dataNascimento,nivelHabilidade,salario));
	}

	@Desafio("definirCapitao")
	public void definirCapitao(Long idJogador) {
		if (!jogadores.containsKey(idJogador)) throw new JogadorNaoEncontradoException();

		Jogador jogadorSelecionado = jogadores.get(idJogador);

		jogadores.entrySet().stream()
				.filter(row -> jogadorSelecionado.getIdTime().equals(row.getValue().getIdTime()) && row.getValue().getIsCapitao())
				.forEach(row -> row.getValue().setIsCapitao(false));

		jogadorSelecionado.setIsCapitao(true);
		jogadores.put(idJogador, jogadorSelecionado);
	}

	@Desafio("buscarCapitaoDoTime")
	public Long buscarCapitaoDoTime(Long idTime) {
		if (!times.containsKey(idTime)) throw new TimeNaoEncontradoException();

		Optional<Long> idJogador = jogadores.entrySet().stream()
				.filter(row -> idTime.equals(row.getValue().getIdTime()) && row.getValue().getIsCapitao())
				.map(Map.Entry::getKey)
				.findFirst();

		if (!idJogador.isPresent()) {
			throw new CapitaoNaoInformadoException();
		}
		return idJogador.get();

	}

	@Desafio("buscarNomeJogador")
	public String buscarNomeJogador(Long idJogador) {
		if (!jogadores.containsKey(idJogador)) throw new JogadorNaoEncontradoException();

		return jogadores.get(idJogador).getNome();
	}

	@Desafio("buscarNomeTime")
	public String buscarNomeTime(Long idTime) {
		if (!times.containsKey(idTime)) throw new TimeNaoEncontradoException();

		return times.get(idTime).getNome();
	}

	@Desafio("buscarJogadoresDoTime")
	public List<Long> buscarJogadoresDoTime(Long idTime) {
		if (!times.containsKey(idTime)) throw new TimeNaoEncontradoException();

		return jogadores.entrySet().stream()
				.filter(row -> idTime.equals(row.getValue().getIdTime()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Desafio("buscarMelhorJogadorDoTime")
	public Long buscarMelhorJogadorDoTime(Long idTime) {
		if (!times.containsKey(idTime)) throw new TimeNaoEncontradoException();

		Optional<Long> idJogador = jogadores.entrySet().stream()
				.filter(row -> idTime.equals(row.getValue().getIdTime()))
				.max(Comparator.comparingInt(row -> row.getValue().getNivelHabilidade()))
				.map(Map.Entry::getKey);

		if (!idJogador.isPresent()) {
			throw new JogadorNaoEncontradoException();
		}
		return idJogador.get();
	}

	@Desafio("buscarJogadorMaisVelho")
	public Long buscarJogadorMaisVelho(Long idTime) {
		if (!times.containsKey(idTime)) throw new TimeNaoEncontradoException();

		Optional<Long> idJogador = jogadores.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.filter(row -> idTime.equals(row.getValue().getIdTime()))
				.min(Comparator.comparing(row -> row.getValue().getDataNascimento()))
				.map(Map.Entry::getKey);

		if (!idJogador.isPresent()) {
			throw new JogadorNaoEncontradoException();
		}
		return idJogador.get();
	}

	@Desafio("buscarTimes")
	public List<Long> buscarTimes() {

		return times.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Desafio("buscarJogadorMaiorSalario")
	public Long buscarJogadorMaiorSalario(Long idTime) {
		if (!times.containsKey(idTime)) throw new TimeNaoEncontradoException();

		Optional<Long> idJogador = jogadores.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.filter(row -> idTime.equals(row.getValue().getIdTime()))
				.max(Comparator.comparing(row -> row.getValue().getSalario()))
				.map(Map.Entry::getKey);

		if (!idJogador.isPresent()) {
			throw new JogadorNaoEncontradoException();
		}
		return idJogador.get();
	}

	@Desafio("buscarSalarioDoJogador")
	public BigDecimal buscarSalarioDoJogador(Long idJogador) {
		if (!jogadores.containsKey(idJogador)) throw new JogadorNaoEncontradoException();

		return jogadores.get(idJogador).getSalario();
	}

	@Desafio("buscarTopJogadores")
	public List<Long> buscarTopJogadores(Integer top) {

		return jogadores.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.comparing(Jogador::getNivelHabilidade).reversed()))
				.map(Map.Entry::getKey)
				.limit(top)
				.collect(Collectors.toList());
	}

	@Desafio("buscarCorCamisaTimeDeFora")
	public String buscarCorCamisaTimeDeFora(Long idTimeDaCasa, Long idTimeDeFora) {
		if (!times.containsKey(idTimeDaCasa) || !times.containsKey(idTimeDeFora)) throw new TimeNaoEncontradoException();

		String corTimePrincipal = times.get(idTimeDaCasa).getCorUniformePrincipal();
		String corTimeSecundario = times.get(idTimeDeFora).getCorUniformePrincipal();

		if(corTimePrincipal.equals(corTimeSecundario))
			return times.get(idTimeDeFora).getCorUniformeSecundario();

		return times.get(idTimeDeFora).getCorUniformePrincipal();
	}

}
