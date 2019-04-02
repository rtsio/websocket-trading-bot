# BUX Trading Bot
Listens to a BUX websocket and makes trades via the BUX API.

#### To run:

`./gradlew bootRun`

#### To create a trade:

Make a POST to /trade with `Content-Type: application/json`, like so:

```
{
 	"productId": "sb26493",
	"buyPrice": 11700,
	"lowerLimit": 11600,
	"upperLimit": 11800
}
```

Runs on port 8090 by default. Supports multiple trades at once, one per productId (for simplicity).

#### Trading logic

Position will be opened if the current product price is between lower limit and buy price (buy price is treated as buy limit).
Position will be closed if the current product price is either below lower limit or above upper limit.

#### Spring profiles:

* default - uses localhost:8080 BUX server
* beta - uses BUX beta environment API
