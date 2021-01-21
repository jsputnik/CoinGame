# Iwo Sokal

# Gra w monety
Zasady:<br/>
Jest dany rząd n monet o różnych wartościach oraz dwójka graczy. W każdym ruchu gracz dobiera monetę ze skrajnie lewej lub 
prawej strony. Wygrywa gracz o największej sumie monet.

# Cel
Znajdowanie najlepszego możliwego wyniku dla gracza rozpoczynającego przy założeniu, że przeciwnik gra tak samo dobrze.

# Tryby wykonywania
Tryb 1: java -jar CoinGame.jar -m1 [input file name] [output file name]<br/>
Tryb 2: java -jar CoinGame.jar -m2 -n[problem size] [output file name]<br/>
Tryb 3: java -jar CoinGame.jar -m3 -n[problem size] -k[repeats] -step[problem size increase]
 -r[repeats for each problem size [output file name]<br/>
Tryb 4: java -jar CoinGame.jar -m4 -n[problem size] -gen[generator type] [output file name]<br/>

Tryb 1 - czyta dane z pliku na podstawie których tworzy grę/gry i zapisuje wynik do innego pliku<br/>
Tryb 2 - tworzy losową grę o rozmiarze [n] i zapisuje wynik do innego pliku<br/>
Tryb 3 - tworzy [k] losowych gier o stopniowo rosnącym rozmiarze. Pierwsza gra ma rozmiar [n]. Dla danego rozmaru [n] jest
tworzonych [r] losowych gier, a następnie rozmiar [n] problemu jest zwiększany o [step]. Wyniki są zapisywane do pliku.<br/>
Tryb 4 - tworzy szczególny przypadek gry [gen] o rozmiarze [n] i zapisuje wynik do pliku. Dostępne są dwa przypadki:<br/>
-asc (dla n = 5: 1, 2, 3, 4, 5)<br/>
-desc (dla n = 5: 5, 4, 3, 2, 1)<br/>

# Format danych
Dane wejściowe są wymagane jedynie dla trybu 1. Są podawane w postaci szeregu kolejnych liczb reprezentujących wartości 
kolejnych monet, (program nie sprawdza poprawności wprowadzonych danych), znak nowej linii (\n) oznacza zakończenie 
łańcucha danych dla danego problemu. Sprawdzony sposób wprowadzania danych, przykład dla 2 gier o rozmiarach n = 5 i n = 6:<br/>
[2, 4, 1, 5, 3]<br/>
[6, 3, 1, 2, 4, 5]<br/>

# Dane wyjściowe
Wyniki zawierają:<br/>
- rozmiar problemu [n]
- początkową zawartość tablicy reprezentującej rząd monet
- punkty gracza rozpoczynającego po zakończeniu gry
- punkty przeciwnika po zakończeniu gry
- strategia gry krok po kroku w postaci tablic reprezentujących rzędy monet
- czas w sekundach wykonania algorytmu
Uwagi:<br/>
Strategia gry jest tworzona tylko dla gier o rozmiarze n < 50.<br/>
Stan na początku gry w postaci tablicy monet również jest zapisywany do pliku tylko dla gier o rozmiarze n < 50.<br/>
Czas wykonania jest mierzony jedynie dla trybu 3 oraz 4.<br/>

Format w jakim wyniki są zapisywane do pliku:<br/>
Problem size = [n]<br/>
Starting coin row:<br/>
[game.starting_coins]<br/>
Results at the end:<br/>
Player points: [game.player_points]<br/>
Opponent points: [game.opponent_points]<br/>
Game strategy:<br/>
[game.game_strategy]<br/>
Elapsed time: [czas w sekundach]<br/>

Wyniki w postaci wielkości gry, punktów obu graczy po zakończeniu gry oraz, jeżeli był mierzony, czasu trwania algorytmu są 
również wyświetlane na konsoli.<br/>

# Rozwiązanie
W programie użyłem algorytmu MinMax z odcięciem alfa-beta, które znajduje najlepsze możliwe posunięcia dla badanego drzewa 
gry.
# Struktury danych
Drzewo jest tworzone przed uruchomieniem samego algorytmu, jego wysokość jest zależna od zmiennej max_tree_height w 
klasie Game (ustawiona domyślnie na wartość 5, ponieważ dla zbyt dużych drzew nie starcza pamięci). W przypadku gdy 
rozmiar problemu jest większy niż wysokość drzewa, tworzone są dodatkowe drzewa. Każde nowe drzewo jest budowane od stanu, 
który znalazł algorytm MinMax jako optymalny. Drzewo składa się z tablicy węzłów, w skład których wchodzą:
- tablica monet po wykonaniu ruchu reprezentowanego przez węzeł
- różnica punktów między oboma graczami po wykonaniu danego ruchu
- ilość punktów gracza rozpoczynającego po wykonaniu danego ruchu
- ilość punktów przeciwnika po wykonaniu danego ruchu
- runda, w trakcie której został wykonany dany ruch
- indeks, służący przede wszystkim do szybkiego znajdywania rodziców i dzieci oraz samego węzła

# Algorytmy
W rzeczywistości zostały zaimplementowane dwa algorytmy:
- pełny MinMax działający na całym drzewie. Nie jest jednak używany w programie, ponieważ działa on jedynie dla 
problemów o bardzo małej wielkości (n < 20), co wynika z ograniczenia pamięciowego.
- MinMax z odcięciem alfa-beta używany w programie. Podobnie jak pełny MinMax operuje on na danym drzewie, natomiast w 
przeciwieństwie do niego nie przegląda każdego węzła, a jedynie te które mają szansę być lepsze z punktu widzenia gracza 
wykonującego dany ruch. Kolejną zaletą jest to, że kończy działanie zarówno po znalezieniu węzła terminalnego, jak i po 
osiągnięciu odpowiedniego poziomu w drzewie, dzięki czemu można go uruchamiać dla wielu drzew wchodzących w skład tej samej 
gry.

# Pliki źrodłowe
Projekt składa się z 4 plików .java:
- Game.java - implementacja funkcji main, generatorów, algorytmu oraz funkcji pomocniczych wspomagających działanie algorytmu 
oraz przechowywanie wyników
- Turn.java - prosty enum, informujący o tym czyja jest w danym stanie tura, używany w algorytmie
- Tree.java - drzewo gry, składające się z tablicy węzłów zawierające funkcje odpowiedzialne za budowanie drzewa
- Node.java - węzeł w drzewie, reprezentujący ruch (stan) danego gracza zawierający funkcje dotyczące samego węzła

# Ograniczenia
Największa możliwa wysokość drzewa to k = 20, natomiast im większy rozmiar problemu n, tym mniejsze musi być k, ponieważ 
wraz ze wzrostem n zwiększa się zajętość pamięci przez węzeł.<br/>
Największy rozmiar problemu możliwy do rozwiązania dla drzew o wysokościach równych 10 wynosi między 31000 a 32000.
