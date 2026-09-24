# Funksjonalitet per v0.0.2

## Datatyper
- streng

## Innebygde funskjoner
`skriv()

## Språkets syntaks
**Kommentarer:**
```språk
//
/* */
```
**Variabler:**
```språk
set x: streng = "Hei, verden!"; // ikke muterbar
mut y: streng = "Hei, Norge!";  // muterbar, ikke null-bar
mut z: streng? = "Hei, null!";  // muterbar, null-bar
mut æ: streng?;                 // muterbar, satt til null
```

**Eksempel:**
```språk
mut x_mut: streng = "Jeg er muterbar";
x_mut = "Nå har jeg blitt endret";
skriv(x_mut);

y_null-bar: streng? = "Jeg kan være null";
skriv(y_null-bar);  //! Kompileringsfeil! Kan ikke kalle skriv på poteniell null-streng

set z_ikke_muterbar: streng?;  //! Kompileringsfeil! variabelet vil foraltid være null
```

