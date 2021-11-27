#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <assert.h>
#include <pthread.h>


static int threads = 1;
static int round = 0;
static int rounds = 0;

struct barrier{
    pthread_mutex_t mutex; 
    pthread_cond_t cond;  
    int threads;      
    int round;          
} barrier;

static void enter_barrier(long n){ 
    pthread_mutex_lock(&barrier.mutex);
    int my_round = barrier.round;

    if (++barrier.threads < threads){  
        while (my_round == round){
            pthread_cond_wait(&barrier.cond, &barrier.mutex); 
        }
    } else {
        barrier.threads = 0;
        barrier.round++;
        round++;
        printf("Thread %li opens barrier of round %i\n", n, round);
        fflush(stdout);
        pthread_cond_broadcast(&barrier.cond); 
    }
    pthread_mutex_unlock(&barrier.mutex);
}

static void* thread(void* num){
    long n = (long) num;
    
    for (int i = 0; i < rounds; i++){
        assert (i == barrier.round);
        enter_barrier(n);
        usleep(random() % 100);
    }
}

int main(int argc, char *argv[]){
    pthread_t* pthreads;
    void* value;

    if (argc < 3) {
        fprintf(stderr, "Usage: %s threads rounds\n", argv[0]);
        exit(-1);
    }
    threads = atoi(argv[1]);
    rounds = atoi(argv[2]);
    srandom(random() % 20);
    pthreads = malloc(sizeof(pthread_t) * threads);
    

    assert(pthread_mutex_init(&barrier.mutex, NULL) == 0);
    assert(pthread_cond_init(&barrier.cond, NULL) == 0);
    barrier.threads = 0;

    for(long i = 0; i < threads; i++) {
        // stworz nowy watek
        assert(pthread_create(&pthreads[i], NULL, thread, (void*) i) == 0);
    }

    for(long i = 0; i < threads; i++) {
        assert(pthread_join(pthreads[i], &value) == 0);
    }

    free(pthreads);
}