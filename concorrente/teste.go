package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

// Versão 1: Pipeline com produtor e consumidor executando continuamente
func producer(out chan<- int) {
	rand.Seed(time.Now().UnixNano())
	for {
		out <- rand.Intn(10)
	}
}

func consumer(in <-chan int) {
	for num := range in {
		if num%2 == 0 {
			fmt.Println("Par:", num)
		}
	}
}

// Versão 2: Produtor gera 10.000 números e finaliza
func producerLimited(out chan<- int, count int) {
	rand.Seed(time.Now().UnixNano())
	for i := 0; i < count; i++ {
		out <- rand.Intn(10)
	}
	close(out)
}

// Versão 3: Duas goroutines produtoras gerando quantidades aleatórias de números
func producerRandom(out chan<- int, wg *sync.WaitGroup) {
	defer wg.Done()
	rand.Seed(time.Now().UnixNano())
	n := rand.Intn(5000) + 5000 // Gera entre 5000 e 10000 números
	for i := 0; i < n; i++ {
		out <- rand.Intn(10)
	}
}

// Versão 4: Uso de canais unidirecionais e bufferizados
func producerBuffered(out chan<- int, wg *sync.WaitGroup) {
	defer wg.Done()
	rand.Seed(time.Now().UnixNano())
	n := rand.Intn(5000) + 5000
	for i := 0; i < n; i++ {
		out <- rand.Intn(10)
	}
}

func main() {
	// Versão 1 - Produtor e consumidor contínuos
	// ch := make(chan int)
	// go producer(ch)
	// go consumer(ch)
	// select {}

	// Versão 2 - Produtor gera 10.000 números
	// ch := make(chan int)
	// go producerLimited(ch, 10000)
	// consumer(ch)

	// Versão 3 - Dois produtores e um consumidor
	// ch := make(chan int)
	// var wg sync.WaitGroup
	// wg.Add(2)
	// go producerRandom(ch, &wg)
	// go producerRandom(ch, &wg)
	// go func() {
	// 	wg.Wait()
	// 	close(ch)
	// }()
	// consumer(ch)

	// Versão 4 - Canais unidirecionais e bufferizados
	ch := make(chan int, 100)
	var wg sync.WaitGroup
	wg.Add(2)
	go producerBuffered(ch, &wg)
	go producerBuffered(ch, &wg)
	go func() {
		wg.Wait()
		close(ch)
	}()
	consumer(ch)
}

