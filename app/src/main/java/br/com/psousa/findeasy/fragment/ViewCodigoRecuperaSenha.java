package br.com.psousa.findeasy.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.glomadrian.codeinputlib.CodeInput;

import java.util.Arrays;

import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.util.AndroidUtils;
import br.com.psousa.findeasy.util.Mask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCodigoRecuperaSenha extends Fragment {


    public ViewCodigoRecuperaSenha() {
        // Required empty public constructor
    }

    private CodeInput mEdtCodigo;
    //private TextWatcher mCodigoMask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_codigo_recupera_senha, container, false);

        //mEdtCodigo = (EditText) view.findViewById(R.id.edt_recupera_senha_codigo);

        // mCodigoMask = Mask.insert("####-####", mEdtCodigo);
        //mEdtCodigo.addTextChangedListener(mCodigoMask);

        mEdtCodigo = (CodeInput) view.findViewById(R.id.edt_recupera_senha_codigo);
        return view;
    }

    public String getCodigo(){
        if (mEdtCodigo == null ||
                Arrays.toString(mEdtCodigo.getCode()).isEmpty()){
            AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.informe_o_codigo));
            return "";
        } else {
            if ((Arrays.toString(mEdtCodigo.getCode()).trim().length() < 7)) {
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.codigo_invalido));
                return "";
            }
        }
        if (mEdtCodigo != null){
            String codigo = Arrays.toString(mEdtCodigo.getCode()).replace("[","").replace(",","").replace("]","").replace(" ","");
            codigo = codigo.substring(0, 4) + "-" + codigo.substring(4);
            return codigo;
        } else {
            return "";
        }
    }

}
