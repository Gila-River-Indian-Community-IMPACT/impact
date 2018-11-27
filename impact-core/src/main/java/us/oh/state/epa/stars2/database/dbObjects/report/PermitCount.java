package us.oh.state.epa.stars2.database.dbObjects.report;


public class PermitCount implements java.io.Serializable {

    private String _doLAAName;
    private int _pbrFinal;
    private int _ptioFinal;
    private int _tvptiFinal;
    private int _tvptoFinal;
    private int _rpcFinal;
    private int _rpeFinal;
    private int _rprFinal;
    private int _regFinal;
    private int _stateFinal;

    private int _pbrDenied;
    private int _ptioDenied;
    private int _tvptiDenied;
    private int _tvptoDenied;
    private int _rpcDenied;
    private int _rpeDenied;
    private int _rprDenied;
    private int _regDenied;
    private int _stateDenied;

    private int _pbrOnTimeCount;
    private int _pbrOnTimeAverage;
    private int _ptioOnTimeCount;
    private int _ptioOnTimeAverage;
    private int _tvptiOnTimeCount;
    private int _tvptiOnTimeAverage;
    private int _tvptoOnTimeCount;
    private int _tvptoOnTimeAverage;
    private int _rpcOnTimeCount;
    private int _rpcOnTimeAverage;
    private int _rpeOnTimeCount;
    private int _rpeOnTimeAverage;
    private int _rprOnTimeCount;
    private int _rprOnTimeAverage;
    private int _regOnTimeCount;
    private int _regOnTimeAverage;
    private int _stateOnTimeCount;
    private int _stateOnTimeAverage;

    private int _pbrOverTimeCount;
    private int _pbrOverTimeAverage;
    private int _ptioOverTimeCount;
    private int _ptioOverTimeAverage;
    private int _tvptiOverTimeCount;
    private int _tvptiOverTimeAverage;
    private int _tvptoOverTimeCount;
    private int _tvptoOverTimeAverage;
    private int _rpcOverTimeCount;
    private int _rpcOverTimeAverage;
    private int _rpeOverTimeCount;
    private int _rpeOverTimeAverage;
    private int _rprOverTimeCount;
    private int _rprOverTimeAverage;
    private int _regOverTimeCount;
    private int _regOverTimeAverage;
    private int _stateOverTimeCount;
    private int _stateOverTimeAverage;

    public PermitCount(String doLAAName) {

        _doLAAName = doLAAName;

    } // END: public PermitCount(String doLAAName)

    public final String getDoLaaName() {
        return _doLAAName;
    }

    public final int getFinalPbr() {
        return _pbrFinal;
    }

    public final void setFinalPbr(int pbr) {
        _pbrFinal = pbr;
    }

    public final int getFinalPtio() {
        return _ptioFinal;
    }

    public final void setFinalPtio(int ptio) {
        _ptioFinal = ptio;
    }

    public final int getFinalTvPti() {
        return _tvptiFinal;
    }

    public final void setFinalTvPti(int tvpti) {
        _tvptiFinal = tvpti;
    }

    public final int getFinalTvPto() {
        return _tvptoFinal;
    }

    public final void setFinalTvPto(int tvpto) {
        _tvptoFinal = tvpto;
    }

    public final int getFinalRpc() {
        return _rpcFinal;
    }

    public final void setFinalRpc(int rpc) {
        _rpcFinal = rpc;
    }

    public final int getFinalRpe() {
        return _rpeFinal;
    }

    public final void setFinalRpe(int rpe) {
        _rpeFinal = rpe;
    }

    public final int getFinalRpr() {
        return _rprFinal;
    }

    public final void setFinalRpr(int rpr) {
        _rprFinal = rpr;
    }

    public final int getFinalReg() {
        return _regFinal;
    }

    public final void setFinalReg(int reg) {
        _regFinal = reg;
    }

    public final int getFinalStatePto() {
        return _stateFinal;
    }

    public final void setFinalStatePto(int state) {
        _stateFinal = state;
    }

    public final int getFinalTotalCount() {

        return _pbrFinal + _ptioFinal + +_tvptiFinal + _tvptoFinal 
            + _rpcFinal + _rpeFinal + _rprFinal + _regFinal + _stateFinal;

    } // END: public int getFinalTotalCount()

    public final int getDeniedPbr() {
        return _pbrDenied;
    }

    public final void setDeniedPbr(int pbr) {
        _pbrDenied = pbr;
    }

    public final int getDeniedPtio() {
        return _ptioDenied;
    }

    public final void setDeniedPtio(int ptio) {
        _ptioDenied = ptio;
    }

    public final int getDeniedTvPti() {
        return _tvptiDenied;
    }

    public final void setDeniedTvPti(int tvpti) {
        _tvptiDenied = tvpti;
    }

    public final int getDeniedTvPto() {
        return _tvptoDenied;
    }

    public final void setDeniedTvPto(int tvpto) {
        _tvptoDenied = tvpto;
    }

    public final int getDeniedRpc() {
        return _rpcDenied;
    }

    public final void setDeniedRpc(int rpc) {
        _rpcDenied = rpc;
    }

    public final int getDeniedRpe() {
        return _rpeDenied;
    }

    public final void setDeniedRpe(int rpe) {
        _rpeDenied = rpe;
    }

    public final int getDeniedRpr() {
        return _rprDenied;
    }

    public final void setDeniedRpr(int rpr) {
        _rprDenied = rpr;
    }

    public final int getDeniedReg() {
        return _regDenied;
    }

    public final void setDeniedReg(int reg) {
        _regDenied = reg;
    }

    public final int getDeniedStatePto() {
        return _stateDenied;
    }

    public final void setDeniedStatePto(int state) {
        _stateDenied = state;
    }

    public final int getDeniedTotalCount() {

        return _pbrDenied + _ptioDenied + +_tvptiDenied + _tvptoDenied 
            + _rpcDenied + _rpeDenied + _rprDenied + _regDenied + _stateDenied;

    } // END: public int getDeniedTotalCount()

    public final int getPbrOnTimeCount() {
        return _pbrOnTimeCount;
    }

    public final void setPbrOnTimeCount(int count) {
        _pbrOnTimeCount = count;
    }

    public final int getPtioOnTimeCount() {
        return _ptioOnTimeCount;
    }

    public final void setPtioOnTimeCount(int count) {
        _ptioOnTimeCount = count;
    }

    public final int getTvPtiOnTimeCount() {
        return _tvptiOnTimeCount;
    }

    public final void setTvPtiOnTimeCount(int count) {
        _tvptiOnTimeCount = count;
    }

    public final int getTvPtoOnTimeCount() {
        return _tvptoOnTimeCount;
    }

    public final void setTvPtoOnTimeCount(int count) {
        _tvptoOnTimeCount = count;
    }

    public final int getRpcOnTimeCount() {
        return _rpcOnTimeCount;
    }

    public final void setRpcOnTimeCount(int count) {
        _rpcOnTimeCount = count;
    }

    public final int getRpeOnTimeCount() {
        return _rpeOnTimeCount;
    }

    public final void setRpeOnTimeCount(int count) {
        _rpeOnTimeCount = count;
    }

    public final int getRprOnTimeCount() {
        return _rprOnTimeCount;
    }

    public final void setRprOnTimeCount(int count) {
        _rprOnTimeCount = count;
    }

    public final int getRegOnTimeCount() {
        return _regOnTimeCount;
    }

    public final void setRegOnTimeCount(int count) {
        _regOnTimeCount = count;
    }

    public final int getStateOnTimeCount() {
        return _stateOnTimeCount;
    }

    public final void setStateOnTimeCount(int count) {
        _stateOnTimeCount = count;
    }

    public final int getPbrOverTimeCount() {
        return _pbrOverTimeCount;
    }

    public final void setPbrOverTimeCount(int count) {
        _pbrOverTimeCount = count;
    }

    public final int getPtioOverTimeCount() {
        return _ptioOverTimeCount;
    }

    public final void setPtioOverTimeCount(int count) {
        _ptioOverTimeCount = count;
    }

    public final int getTvPtiOverTimeCount() {
        return _tvptiOverTimeCount;
    }

    public final void setTvPtiOverTimeCount(int count) {
        _tvptiOverTimeCount = count;
    }

    public final int getTvPtoOverTimeCount() {
        return _tvptoOverTimeCount;
    }

    public final void setTvPtoOverTimeCount(int count) {
        _tvptoOverTimeCount = count;
    }

    public final int getRpcOverTimeCount() {
        return _rpcOverTimeCount;
    }

    public final void setRpcOverTimeCount(int count) {
        _rpcOverTimeCount = count;
    }

    public final int getRpeOverTimeCount() {
        return _rpeOverTimeCount;
    }

    public final void setRpeOverTimeCount(int count) {
        _rpeOverTimeCount = count;
    }

    public final int getRprOverTimeCount() {
        return _rprOverTimeCount;
    }

    public final void setRprOverTimeCount(int count) {
        _rprOverTimeCount = count;
    }

    public final int getRegOverTimeCount() {
        return _regOverTimeCount;
    }

    public final void setRegOverTimeCount(int count) {
        _regOverTimeCount = count;
    }

    public final int getStateOverTimeCount() {
        return _stateOverTimeCount;
    }

    public final void setStateOverTimeCount(int count) {
        _stateOverTimeCount = count;
    }

    public final int getPbrOnTimeAverage() {
        return _pbrOnTimeAverage;
    }

    public final void setPbrOnTimeAverage(int average) {
        _pbrOnTimeAverage = average;
    }

    public final int getPtioOnTimeAverage() {
        return _ptioOnTimeAverage;
    }

    public final void setPtioOnTimeAverage(int average) {
        _ptioOnTimeAverage = average;
    }

    public final int getTvPtiOnTimeAverage() {
        return _tvptiOnTimeAverage;
    }

    public final void setTvPtiOnTimeAverage(int average) {
        _tvptiOnTimeAverage = average;
    }

    public final int getTvPtoOnTimeAverage() {
        return _tvptoOnTimeAverage;
    }

    public final void setTvPtoOnTimeAverage(int average) {
        _tvptoOnTimeAverage = average;
    }

    public final int getRpcOnTimeAverage() {
        return _rpcOnTimeAverage;
    }

    public final void setRpcOnTimeAverage(int average) {
        _rpcOnTimeAverage = average;
    }

    public final int getRpeOnTimeAverage() {
        return _rpeOnTimeAverage;
    }

    public final void setRpeOnTimeAverage(int average) {
        _rpeOnTimeAverage = average;
    }

    public final int getRprOnTimeAverage() {
        return _rprOnTimeAverage;
    }

    public final void setRprOnTimeAverage(int average) {
        _rprOnTimeAverage = average;
    }

    public final int getRegOnTimeAverage() {
        return _regOnTimeAverage;
    }

    public final void setRegOnTimeAverage(int average) {
        _regOnTimeAverage = average;
    }

    public final int getStateOnTimeAverage() {
        return _stateOnTimeAverage;
    }

    public final void setStateOnTimeAverage(int average) {
        _stateOnTimeAverage = average;
    }

    public final int getPbrOverTimeAverage() {
        return _pbrOverTimeAverage;
    }

    public final void setPbrOverTimeAverage(int average) {
        _pbrOverTimeAverage = average;
    }

    public final int getPtioOverTimeAverage() {
        return _ptioOverTimeAverage;
    }

    public final void setPtioOverTimeAverage(int average) {
        _ptioOverTimeAverage = average;
    }

    public final int getTvPtiOverTimeAverage() {
        return _tvptiOverTimeAverage;
    }

    public final void setTvPtiOverTimeAverage(int average) {
        _tvptiOverTimeAverage = average;
    }

    public final int getTvPtoOverTimeAverage() {
        return _tvptoOverTimeAverage;
    }

    public final void setTvPtoOverTimeAverage(int average) {
        _tvptoOverTimeAverage = average;
    }

    public final int getRpcOverTimeAverage() {
        return _rpcOverTimeAverage;
    }

    public final void setRpcOverTimeAverage(int average) {
        _rpcOverTimeAverage = average;
    }

    public final int getRpeOverTimeAverage() {
        return _rpeOverTimeAverage;
    }

    public final void setRpeOverTimeAverage(int average) {
        _rpeOverTimeAverage = average;
    }

    public final int getRprOverTimeAverage() {
        return _rprOverTimeAverage;
    }

    public final void setRprOverTimeAverage(int average) {
        _rprOverTimeAverage = average;
    }

    public final int getRegOverTimeAverage() {
        return _regOverTimeAverage;
    }

    public final void setRegOverTimeAverage(int average) {
        _regOverTimeAverage = average;
    }

    public final int getStateOverTimeAverage() {
        return _stateOverTimeAverage;
    }

    public final void setStateOverTimeAverage(int average) {
        _stateOverTimeAverage = average;
    }

} // END: public class PermitCount implements java.io.Serializable
