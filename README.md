# Afirme Payment Android SDK

Afirme Payment Android SDK is a library that allows developers to easily connect to the Afirme CREDITCARDS API


## Installation

### Android Studio (or Gradle)

Add it in your root build.gradle at the end of repositories:

    allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
        }
        
Add this line to your app's `build.gradle` inside the `dependencies` section:

    implementation 'com.github.afirme-shieldgate:afirme-shieldgate-android:1.2.9'

### ProGuard

If you're planning on optimizing your app with ProGuard, make sure that you exclude the Afirme bindings. You can do this by adding the following to your app's `proguard.cfg` file:

    -keep class com.afirme.payment.** { *; }

## Usage

### Using the CardMultilineWidget

You can add a widget to your apps that easily handles the UI states for collecting card data.

First, add the CardMultilineWidget to your layout.

```xml
<com.afirme.payment.view.CardMultilineWidget
        android:id="@+id/card_multiline_widget"
        android:layout_alignParentTop="true"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

You can customize the view with this tags:

```xml
app:shouldShowPostalCode="true"
app:shouldShowLogo="true"
app:shouldShowCardHolderName="true"
app:shouldShowScanCard="true"
```

In order to use any of this tags, you'll need to enable the app XML namespace somewhere in the layout.

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```

To get a `Card` object from the `CardMultilineWidget`, you ask the widget for its card.

```java
Card cardToSave = cardWidget.getCard();
if (cardToSave == null) {
    Alert.show(mContext,
        "Error",
        "Invalid Card Data");
    return;
}
```

If the returned `Card` is null, error states will show on the fields that need to be fixed.

Once you have a non-null `Card` object from the widget, you can call [addCard](#addCard).

### Init library
You should initialize the library on your Application or in your first Activity.

```java
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.afirme.payment.Payment;
import com.afirme.payment.example.utils.Constants;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      /**
       * Init library
       *
       * @param test_mode false to use production environment
       * @param client_app_code provided by Afirme.
       * @param client_app_key provided by Afirme.
       */
      Payment.setEnvironment(Constants.IS_TEST_MODE, Constants.CLIENT_APP_CODE, Constants.CLIENT_APP_KEY);


       // In case you have your own Fraud Risk Merchant Id
       //Payment.setRiskMerchantId(1000);
       // Note: for most of the devs, that's not necessary.
    }
}
```

### addCard

addCard converts sensitive card data to a single-use token which you can safely pass to your server to charge the user.

```java
Payment.addCard(mContext, uid, email, cardToSave, new TokenCallback() {

    public void onSuccess(Card card) {

        if(card != null){
            if(card.getStatus().equals("valid")){
                Alert.show(mContext,
                        "Card Successfully Added",
                        "status: " + card.getStatus() + "\n" +
                                "Card Token: " + card.getToken() + "\n" +
                                "transaction_reference: " + card.getTransactionReference());

            } else if (card.getStatus().equals("review")) {
                Alert.show(mContext,
                        "Card Under Review",
                        "status: " + card.getStatus() + "\n" +
                                "Card Token: " + card.getToken() + "\n" +
                                "transaction_reference: " + card.getTransactionReference());

            } else {
                Alert.show(mContext,
                        "Error",
                        "status: " + card.getStatus() + "\n" +
                                "message: " + card.getMessage());
            }


        }

        //TODO: Create charge or Save Token to your backend
    }

    public void onError(PaymentError error) {
        Alert.show(mContext,
                "Error",
                "Type: " + error.getType() + "\n" +
                        "Help: " + error.getHelp() + "\n" +
                        "Description: " + error.getDescription());

        //TODO: Handle error
    }

});
```

The first argument to addCard is mContext (Context).
+ mContext. Context of the Current Activity

The second argument to addCard is uid (String).
+ uid Customer identifier. This is the identifier you use inside your application; you will receive it in notifications.

The third argument to addCard is email (String).
+ email Email of the customer

The fourth argument to addCard is a Card object. A Card contains the following fields:

+ number: card number as a string without any separators, e.g. '4242424242424242'.
+ holderName: cardholder name.
+ expMonth: integer representing the card's expiration month, e.g. 12.
+ expYear: integer representing the card's expiration year, e.g. 2013.
+ cvc: card security code as a string, e.g. '123'.
+ type:

The fifth argument tokenCallback is a callback you provide to handle responses from Afirme.
It should send the token to your server for processing onSuccess, and notify the user onError.

Here's a sample implementation of the token callback:
```java
Payment.addCard(
    mContext, uid, email, cardToSave,
    new TokenCallback() {
        public void onSuccess(Card card) {
            // Send token to your own web service
            MyServer.chargeToken(card.getToken());
        }
        public void onError(PaymentError error) {
            Toast.makeText(getContext(),
                error.getDescription(),
                Toast.LENGTH_LONG).show();
        }
    }
);
```

`addCard` is an asynchronous call – it returns immediately and invokes the callback on the UI thread when it receives a response from Afirme's servers.

### getSessionId

The Session ID is a parameter Afirme use for fraud purposes.
Call this method if you want to Collect your user's Device Information.

```java
String session_id = Payment.getSessionId(mContext);
```

Once you have the Session ID, you can pass it to your server to charge the user.

### Client-side validation helpers

The Card object allows you to validate user input before you send the information to Afirme.

#### validateNumber

Checks that the number is formatted correctly and passes the [Luhn check](http://en.wikipedia.org/wiki/Luhn_algorithm).

#### validateExpiryDate

Checks whether or not the expiration date represents an actual month in the future.

#### validateCVC

Checks whether or not the supplied number could be a valid verification code.

#### validateCard

Convenience method to validate card number, expiry date and CVC.

### Getting started with the Android example app

Note: the app require an [Android SDK](https://developer.android.com/studio/index.html) and [Gradle](https://gradle.org/) to build and run.


### Building and Running the AfirmeStore

Before you can run the AfirmeStore application, you need to provide it with your Afirme Credentials and a Sample Backend.

1. If you don't have any Credentials yet, please ask your contact on Afirme Team for it.
2. Head to https://github.com/afirme-shieldgate/example-java-backend and click "Deploy to Heroku" (you may have to sign up for a Heroku account as part of this process). Provide your Afirme Server Credentials `SERVER_APP_CODE` and  `SERVER_APP_KEY` fields under 'Env'. Click "Deploy for Free".
3. Open the project on Android Studio.
4. Replace the `CLIENT_APP_CODE` and `CLIENT_APP_KEY` constants in Constants.java with your own Afirme Client Credentials.
5. Replace the `BACKEND_URL` variable in the Constants.java file with the app URL Heroku provides you with (e.g. "https://my-example-app.herokuapp.com")
6. Run the Project.

Important Note: if you only have one APP_CODE, please asume that it's your `SERVER_APP_CODE`. So you need to ask your contact on Afirme Team for your `CLIENT_APP_CODE`.

===================

El SDK Android de pagos Afirme es una biblioteca que permite a los desarrolladores conectarse fácilmente al API de tarjetas de Crédito de Afirme.



## Instalación

### Android Studio (o Gradle)

Agregue lo siguiente en build.gradle al final de repositories:

    allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
        }
        
Agregue esta linea a sus app's `build.gradle` en la seccion  `dependencies`

    implementation 'com.github.afirme-shieldgate:afirme-shieldgate-android:1.2.9'

### ProGuard

Si usted está planeando optimiar su aplicación con ProGuard, asegúrese de excluir los enlaces de Afirme. Usted puede realizarlo añadiendo lo siguiente al archivo `proguard.cfg`  de su app:

     -keep class com.afirme.payment.** { *; }

## Uso

### Utilizando el CardMultilineWidget

Puede agregar un widget a sus aplicaciones que maneje fácilmente los estados de la interfaz de usuario para recopilar datos de la tarjeta.

Primero, añada el CardMultilineWidget a su layout.


```xml
<com.afirme.payment.view.CardMultilineWidget
        android:id="@+id/card_multiline_widget"
        android:layout_alignParentTop="true"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

Ustede puede personalizar la vista con las siguientes etiquetas:

```xml
app:shouldShowPostalCode="true"
app:shouldShowLogo="true"
app:shouldShowCardHolderName="true"
app:shouldShowScanCard="true"
```

Para usar cualquiera de estas etiquetas, deberá habilitar el espacio de nombres XML de la aplicación en algún lugar del layout.

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```

Para obtener un objeto `Card` del` CardMultilineWidget`, pidale al widget su tarjeta.

```java
Card cardToSave = cardWidget.getCard();
if (cardToSave == null) {
    Alert.show(mContext,
        "Error",
        "Invalid Card Data");
    return;
}
```
Si la `Card` devuelta es null , se mostrarán estados de error en los campos que deben corregirse.

Una vez que tenga un objeto `Card` no null regresado desde el widget, puede llamar a [addCard](#addCard).


### Inicializar Biblioteca

Usted debe inicializar la biblioteca en su Aplicación en su primera actividad.
You should initialize the library on your Application or in your first Activity.


```java
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.afirme.payment.Payment;
import com.afirme.payment.example.utils.Constants;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      /**
       * Init library
       *
       * @param test_mode false to use production environment
       * @param client_app_code provided by Afirme.
       * @param client_app_key provided by Afirme.
       */
      Payment.setEnvironment(Constants.IS_TEST_MODE, Constants.CLIENT_APP_CODE, Constants.CLIENT_APP_KEY);


       // In case you have your own Fraud Risk Merchant Id
       //Payment.setRiskMerchantId(1000);
       // Note: for most of the devs, that's not necessary.
    }
}
```

### addCard

addCard convierte datos confidenciales de la tarjeta en un token de un solo uso que puede pasar de forma segura a su servidor para realizar el cobro al usuario.


```java
Payment.addCard(mContext, uid, email, cardToSave, new TokenCallback() {

    public void onSuccess(Card card) {

        if(card != null){
            if(card.getStatus().equals("valid")){
                Alert.show(mContext,
                        "Card Successfully Added",
                        "status: " + card.getStatus() + "\n" +
                                "Card Token: " + card.getToken() + "\n" +
                                "transaction_reference: " + card.getTransactionReference());

            } else if (card.getStatus().equals("review")) {
                Alert.show(mContext,
                        "Card Under Review",
                        "status: " + card.getStatus() + "\n" +
                                "Card Token: " + card.getToken() + "\n" +
                                "transaction_reference: " + card.getTransactionReference());

            } else {
                Alert.show(mContext,
                        "Error",
                        "status: " + card.getStatus() + "\n" +
                                "message: " + card.getMessage());
            }


        }

        //TODO: Create charge or Save Token to your backend
    }

    public void onError(PaymentError error) {
        Alert.show(mContext,
                "Error",
                "Type: " + error.getType() + "\n" +
                        "Help: " + error.getHelp() + "\n" +
                        "Description: " + error.getDescription());

        //TODO: Handle error
    }

});
```

El primer argumento del addCard es mContext (Context).
+ mContext. Context de la Actividad actual.

El segundo argumento del addCard es uid (Cadena).
+ uid Identificador de comprador. Este es el identificador que usa dentro de su aplicación; lo recibirá en las notificaciones.

El tercer argumento del addCard es el email (Cadena).
+ email Email del comprador

El cuarto argumento del addCard es un objeto de tipo Card. Un objeto Card contiene los siguientes campos:

+ number: número de tarjeta como cadena sin ningún separador, por ejemplo '4242424242424242'.
+ holderName: nombre del tarjehabiente.
+ expMonth: entero que representa el mes de expiración de la tarjeta, por ejemplo 12.
+ expYear: entero que represetna el año de expiración de la tarjeta, por ejemplo 2019.
+ cvc: código de seguridad de la tarjeta, como cadena, por ejemplo '123'.
+ type: el tipo de tarjeta.

El quinto argumento tokenCallBack es un callback que usted provee para manejar las respuestas recibidas de Afirme.

Deberá enviar el token a su servidor para procesar onSuccess y notificar al usuario onError.

Aquí se muestra un ejemplo de implementación de callback del token:
```java
Payment.addCard(
    mContext, uid, email, cardToSave,
    new TokenCallback() {
        public void onSuccess(Card card) {
            // Send token to your own web service
            MyServer.chargeToken(card.getToken());
        }
        public void onError(PaymentError error) {
            Toast.makeText(getContext(),
                error.getDescription(),
                Toast.LENGTH_LONG).show();
        }
    }
);
```

`addCard` es una llamada asíncrona - regresa inmediatamente e invoca el callback en el hilo de la interfaz, cuando recibe una respuesta de los servidores de Afirme.


### getSessionId

El Session ID es un parámetro que Afirme utiliza para fines de antifraude.
Llave esté método cuando quiera recabar la información del dispositivo.

```java
String session_id = Payment.getSessionId(mContext);
```
Una vez que tenga el Session ID, usted podrá pasarlo a su servidor para realizar el cobro a su usuario.


### Herramientas de validación del lado del Cliente
El objeto Card permite validar la información capturada por el usuario antes de enviarla a Afirme.


#### validateNumber

Verifica que el número tenga el formato correcto y pase el algoritmo de [Luhn](http://en.wikipedia.org/wiki/Luhn_algorithm).

#### validateExpiryDate

Comprueba que la fecha de expiración representa una fecha real futura.

#### validateCVC

Comprueba si el número proporcionado podría ser un código de verificación válido o no.

#### validateCard

Método que valida el número de tarjeta, la fecha de expiración y el CVC.


### Introducción a la aplicación de ejemplo de Android

Nota: la aplicación requiere [Android SDK](https://developer.android.com/studio/index.html) y [Gradle](https://gradle.org/) para coinstruir y ejecutar.


### Construyendo y Ejecutando la PaymentStore

Antes de que pueda pueda correr la aplicación PaymentStore, ested necesita proveerla con las credenciales de Afirme y un backend de muestra.

1. Si aún no cuenta con credenciales, pídalas a su contancto en el equipo de Afirme.
2. Dirígase a https://github.com/afirme-shieldgate/example-java-backend y haga click en "Deploy to Heroku" (es posible que tengas que registrarte para obtener una cuenta de Heroku como parte de este proceso). Proporcione sus credenciales de servidor de Afirme `SERVER_APP_CODE` y `SERVER_APP_KEY`en los campos "Env". Haga click en "Deploy for Free".
3. Abra el proyecto en Android Studio.
4. Reemplace las constantes  `CLIENT_APP_CODE` y `CLIENT_APP_KEY` en Constants.java con sus propias credenciales de Afirme.
5. Reemplace la variable `BACKEND_URL` en el archivo Constants.java con la URL de la aplicación que Heroku le proporciona (por ejemplo," https://my-example-app.herokuapp.com ")
6. Ejecute el proyecto.

Nota importante: si solo tiene un APP_CODE, suponga que es su `SERVER_APP_CODE`. Por lo tanto, debe solicitar a su contacto en el equipo de Afirme su `CLIENT_APP_CODE`.
