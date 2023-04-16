composite(1).

prime(2).
prime(X) :-
	X > 2,
	not composite(X).

mark_compose(S, L, R) :-
	L =< R,
	assert(composite(L)),
	Next is L + S,
	mark_compose(S, Next, R).

init(N) :-
	fill_table(2, N).

fill_table(F, L) :-
	prime(F),
	SqrF is F * F,
	SqrF =< L,
	mark_compose(F, SqrF, L),
	fill_table(NextPrime, L).

fill_table(F, L) :-
	F < L,
	Next is F + 1,
	fill_table(Next, L).

prime_divisors(1, []) :- !.

prime_divisors(N, Divisors) :-
    number(N),
    get_num(2, Divisors, N).

prime_divisors(N, Divisors) :-
    not(number(N)),
    get_array(2, Divisors, N).

divis(A, B) :-
    0 is mod(A, B), !.

get_array(_, [], 1) :- !.

get_array(X, [H | T], Ans) :-
    X =< H,
    prime(H),
    get_array(H , T, Tailed),
    Ans is Tailed * H.

get_num(D, [N], N) :-
    N < D * D, !.

get_num(D, [H | T], N) :-
    divis(N, D),
    H is D,
    Next is div(N, D),
    get_num(D, T, Next).

get_num(Divisor, [H | T], N) :-
    \+ divis(N, Divisor),
    Next is Divisor + 1,
    get_num(Next, [H | T], N).

lcm(A, B, LCM) :-
    prime_divisors(A, Ad),
    prime_divisors(B, Bd),
    get_lcm(Ad, Bd, LCM).

get_lcm([], [], 1) :- !.

get_lcm([H | T], [], LCM) :-
    get_lcm(T, [], AnsLCM),
    LCM is AnsLCM * H, !.

get_lcm([], [H | T],  LCM) :-
    get_lcm([], T, AnsLCM),
    LCM is AnsLCM * H, !.

get_lcm([H1 | T1], [H2 | T2], LCM) :-
    H1 < H2,
    get_lcm(T1, [H2 | T2], AnsLCM),
    LCM is AnsLCM * H1, !.

get_lcm([H1 | T1], [H2 | T2], LCM) :-
    H1 > H2,
    get_lcm([H1 | T1], T2, AnsLCM),
    LCM is AnsLCM * H2, !.

get_lcm([H | T1], [H | T2], LCM) :-
    get_lcm(T1, T2, AnsLCM),
    LCM is AnsLCM * H, !.
