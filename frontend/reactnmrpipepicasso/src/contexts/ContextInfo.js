import React, {createContext, Component} from "react";
import SessionProfile from "./SessionProfile";


export const ContextInfo = createContext();

class ContextInfoProvider extends Component {
    state = {
        dirtmp: Math.random().toString(36).substr(2, 10)
    }
    setdirtmp = (d)  => {
        console.log('cambio stato ', d);
        this.setState({dirtmp: d});
    }
    render() {
        return (
            <ContextInfo.Provider value={{...this.state, setdirtmp: this.setdirtmp }}>
                {this.props.children}
            </ContextInfo.Provider>
        );
    }
}

export default ContextInfoProvider;