package main

import (
    "fmt"
        )

func fib(n int) int {
    if n <= 1 {
        return n
    }
    return fib(n-1) + fib(n-2)
}

func status() {
        for {
            fmt.Println("calma,Kayky!")
        }
    }

func main() {
    go status()
    result := fib(50)
    fmt.Println(result)
}
