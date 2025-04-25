package br.edu.ifsuldeminas.mch.calc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private double baseParaPorcentagem = 0;
    private boolean aguardandoPorcentagem = false;
    private double ultimoResultadoValor = 0;

    // Declaração de todas as Views como atributos
    private TextView textViewResultado, textViewUltimaExpressao;
    private Button button0, button1, button2, button3, button4,
            button5, button6, button7, button8, button9,
            buttonSoma, buttonSubtracao, buttonMultiplicacao,
            buttonDivisao, buttonPorcento, buttonVirgula,
            buttonIgual, buttonReset, buttonDelete;

    private StringBuilder expressaoBuilder = new StringBuilder();
    private boolean ultimoResultado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarViews();
        configurarListeners();
    }

    private void inicializarViews() {
        // TextViews
        textViewResultado = findViewById(R.id.textViewResultadoID);
        textViewUltimaExpressao = findViewById(R.id.textViewUltimaExpressaoID);

        button0 = findViewById(R.id.buttonZeroID);
        button1 = findViewById(R.id.buttonUmID);
        button2 = findViewById(R.id.buttonDoisID);
        button3 = findViewById(R.id.buttonTresID);
        button4 = findViewById(R.id.buttonQuatroID);
        button5 = findViewById(R.id.buttonCincoID);
        button6 = findViewById(R.id.buttonSeisID);
        button7 = findViewById(R.id.buttonSeteID);
        button8 = findViewById(R.id.buttonOitoID);
        button9 = findViewById(R.id.buttonNoveID);

        buttonSoma = findViewById(R.id.buttonSomaID);
        buttonSubtracao = findViewById(R.id.buttonSubtracaoID);
        buttonMultiplicacao = findViewById(R.id.buttonMultiplicacaoID);
        buttonDivisao = findViewById(R.id.buttonDivisaoID);
        buttonPorcento = findViewById(R.id.buttonPorcentoID);
        buttonVirgula = findViewById(R.id.buttonVirgulaID);
        buttonIgual = findViewById(R.id.buttonIgualID);
        buttonReset = findViewById(R.id.buttonResetID);
        buttonDelete = findViewById(R.id.buttonDeleteID);
    }

    private void configurarListeners() {
        configurarBotoesNumericos();
        configurarBotoesOperadores();
        configurarBotoesEspeciais();
    }

    private void configurarBotoesNumericos() {
        View.OnClickListener listenerNumeros = v -> {
            if (ultimoResultado) {
                expressaoBuilder.setLength(0);
                ultimoResultado = false;
            }
            Button botao = (Button) v;
            expressaoBuilder.append(botao.getText());
            atualizarDisplay();
        };

        button0.setOnClickListener(listenerNumeros);
        button1.setOnClickListener(listenerNumeros);
        button2.setOnClickListener(listenerNumeros);
        button3.setOnClickListener(listenerNumeros);
        button4.setOnClickListener(listenerNumeros);
        button5.setOnClickListener(listenerNumeros);
        button6.setOnClickListener(listenerNumeros);
        button7.setOnClickListener(listenerNumeros);
        button8.setOnClickListener(listenerNumeros);
        button9.setOnClickListener(listenerNumeros);
    }

    private void configurarBotoesOperadores() {
        View.OnClickListener listenerOperadores = v -> {
            Button botao = (Button) v;
            String operador = botao.getText().toString();

            if (operador.equals("÷")) {
                operador = "/";
            } else if (operador.equals("%")) {
                calcularPorcentagem();
                return;
            }

            if (ultimoResultado) {
                expressaoBuilder.setLength(0);
                expressaoBuilder.append(ultimoResultadoValor);
                ultimoResultado = false;
            }

            if (expressaoBuilder.length() > 0) {
                char ultimoChar = expressaoBuilder.charAt(expressaoBuilder.length() - 1);
                if (isOperador(ultimoChar)) {
                    expressaoBuilder.setCharAt(expressaoBuilder.length() - 1, operador.charAt(0));
                } else {
                    expressaoBuilder.append(operador);
                }
            } else {
                expressaoBuilder.append(operador);
            }

            atualizarDisplay();
        };

        buttonSoma.setOnClickListener(listenerOperadores);
        buttonSubtracao.setOnClickListener(listenerOperadores);
        buttonMultiplicacao.setOnClickListener(listenerOperadores);
        buttonDivisao.setOnClickListener(listenerOperadores);
        buttonPorcento.setOnClickListener(v -> calcularPorcentagem());
    }


    private void configurarBotoesEspeciais() {
        // Vírgula decimal
        buttonVirgula.setOnClickListener(v -> {
            expressaoBuilder.append(".");
            atualizarDisplay();
        });

        // Botão Igual
        buttonIgual.setOnClickListener(v -> calcularResultado());

        // Botão Reset
        buttonReset.setOnClickListener(v -> {
            expressaoBuilder.setLength(0);
            textViewResultado.setText("0");
            textViewUltimaExpressao.setText("");
            ultimoResultado = false;
        });

        // Botão Delete
        buttonDelete.setOnClickListener(v -> {
            if (expressaoBuilder.length() > 0) {
                expressaoBuilder.deleteCharAt(expressaoBuilder.length() - 1);
                atualizarDisplay();
            }
        });
    }

    private void calcularResultado() {
        if (aguardandoPorcentagem) {
            try {
                String porcentagemStr = expressaoBuilder.toString().replace(',', '.');
                double porcentagem = Double.parseDouble(porcentagemStr);
                double resultado = baseParaPorcentagem * (porcentagem / 100);

                expressaoBuilder.setLength(0);
                expressaoBuilder.append(resultado);
                aguardandoPorcentagem = false;
                atualizarDisplay();
                return;

            } catch (Exception e) {
                Log.e("PORCENTAGEM", "Erro ao calcular porcentagem: " + e.getMessage());
                textViewResultado.setText("Erro");
                return;
            }
        }
        try {
            
            String expressao = expressaoBuilder.toString().replace(',', '.');

            if (expressao.length() > 0 && isOperador(expressao.charAt(expressao.length() - 1))) {
                expressao = expressao.substring(0, expressao.length() - 1);
            }

            if (expressao.isEmpty()) return;

            Expression exp = new ExpressionBuilder(expressao).build();
            double resultado = exp.evaluate();

            textViewUltimaExpressao.setText(expressao + " =");
            textViewResultado.setText(String.valueOf(resultado));

            // Armazena o resultado para a próxima operação
            ultimoResultadoValor = resultado;
            ultimoResultado = true;

        } catch (Exception e) {
            Log.e("CALC_ERROR", "Erro: " + e.getMessage());
            textViewResultado.setText("Erro");
        }
    }



    private void calcularPorcentagem() {
        try {
            if (expressaoBuilder.length() == 0) return;

            Expression expr = new ExpressionBuilder(expressaoBuilder.toString()).build();
            baseParaPorcentagem = expr.evaluate();

            expressaoBuilder.setLength(0);
            aguardandoPorcentagem = true;
            atualizarDisplay();

        } catch (Exception e) {
            Log.e("PORCENTAGEM", "Erro ao preparar porcentagem: " + e.getMessage());
            textViewResultado.setText("Erro");
        }

    }

    private boolean isOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private void atualizarDisplay() {
        textViewResultado.setText(expressaoBuilder.toString());
    }
}