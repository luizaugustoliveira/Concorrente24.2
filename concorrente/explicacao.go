package main

import (
	"fmt"       // Pacote para entrada e saída de dados
	"math/rand" // Pacote para geração de números aleatórios
	"sync"      // Pacote para sincronização de goroutines
	"time"      // Pacote para manipulação de tempo
)

// Versão 1: Pipeline com produtor e consumidor executando continuamente
// Função produtora gera números aleatórios e os envia pelo canal
func producer(out chan<- int) {
	rand.Seed(time.Now().UnixNano()) // Inicializa a semente para gerar números aleatórios diferentes a cada execução
	for {
		out <- rand.Intn(10) // Gera um número aleatório entre 0 e 9 e envia para o canal
	}
}

// Função consumidora lê do canal e imprime apenas os números pares
func consumer(in <-chan int) {
	for num := range in { // Loop que lê valores do canal até ele ser fechado
		if num%2 == 0 { // Verifica se o número é par
			fmt.Println("Par:", num) // Imprime os números pares
		}
	}
}

// Versão 2: Produtor gera 10.000 números e finaliza
func producerLimited(out chan<- int, count int) {
	rand.Seed(time.Now().UnixNano()) // Inicializa a semente aleatória
	for i := 0; i < count; i++ {
		out <- rand.Intn(10) // Gera e envia números aleatórios
	}
	close(out) // Fecha o canal após enviar todos os números, sinalizando que não há mais dados
}

// Versão 3: Duas goroutines produtoras gerando quantidades aleatórias de números
func producerRandom(out chan<- int, wg *sync.WaitGroup) {
	defer wg.Done() // Marca a goroutine como concluída ao terminar a execução
	rand.Seed(time.Now().UnixNano()) // Inicializa a semente aleatória
	n := rand.Intn(5000) + 5000 // Gera uma quantidade aleatória entre 5000 e 10000
	for i := 0; i < n; i++ {
		out <- rand.Intn(10) // Envia números aleatórios para o canal
	}
}

// Versão 4: Uso de canais unidirecionais e bufferizados
func producerBuffered(out chan<- int, wg *sync.WaitGroup) {
	defer wg.Done() // Marca a goroutine como concluída
	rand.Seed(time.Now().UnixNano()) // Inicializa a semente aleatória
	n := rand.Intn(5000) + 5000 // Gera uma quantidade aleatória de números
	for i := 0; i < n; i++ {
		out <- rand.Intn(10) // Envia os números para o canal bufferizado
	}
}

func main() {
	// Versão 1 - Produtor e consumidor contínuos
	// ch := make(chan int) // Cria um canal sem buffer
	// go producer(ch) // Inicia o produtor em uma goroutine
	// go consumer(ch) // Inicia o consumidor em outra goroutine
	// select {} // Mantém o programa rodando indefinidamente

	// Versão 2 - Produtor gera 10.000 números
	// ch := make(chan int) // Cria um canal sem buffer
	// go producerLimited(ch, 10000) // Inicia o produtor que gera 10.000 números
	// consumer(ch) // O consumidor lê os números e imprime os pares

	// Versão 3 - Dois produtores e um consumidor
	// ch := make(chan int) // Cria um canal sem buffer
	// var wg sync.WaitGroup // Cria um grupo de espera para sincronização
	// wg.Add(2) // Adiciona duas goroutines ao contador de espera
	// go producerRandom(ch, &wg) // Inicia primeiro produtor
	// go producerRandom(ch, &wg) // Inicia segundo produtor
	// go func() {
	// 	wg.Wait() // Aguarda todos os produtores finalizarem
	// 	close(ch) // Fecha o canal ao finalizar a produção
	// }()
	// consumer(ch) // Inicia o consumidor para ler os números gerados

	// Versão 4 - Canais unidirecionais e bufferizados
	ch := make(chan int, 100) // Cria um canal bufferizado com capacidade de 100 valores
	var wg sync.WaitGroup // Cria um grupo de espera para sincronização
	wg.Add(2) // Adiciona duas goroutines produtoras ao grupo de espera
	go producerBuffered(ch, &wg) // Inicia primeira goroutine produtora
	go producerBuffered(ch, &wg) // Inicia segunda goroutine produtora
	go func() {
		wg.Wait() // Aguarda ambas as goroutines produtoras finalizarem
		close(ch) // Fecha o canal ao finalizar a produção
	}()
	consumer(ch) // O consumidor lê os números do canal e imprime os pares
}

