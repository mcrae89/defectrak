import * as React from 'react';
import { Message, MessageModel } from '@syncfusion/ej2-notifications';
import { DefaultHtmlAttributes } from '@syncfusion/ej2-react-base';
export interface MessageTypecast {
    content?: string | Function | any;
}
/**
 * The React Message component displays messages with severity by differentiating icons and colors to denote the importance and context of the message to the end user.
 * ```html
 * <MessageComponent id='msg' showCloseIcon={true}>Editing is restricted</MessageComponent>
 * ```
 */
export declare class MessageComponent extends Message {
    state: Readonly<{
        children?: React.ReactNode | React.ReactNode[];
    }> & Readonly<MessageModel | DefaultHtmlAttributes | MessageTypecast>;
    setState: any;
    private getDefaultAttributes;
    initRenderCalled: boolean;
    private checkInjectedModules;
    private statelessTemplateProps;
    private templateProps;
    private immediateRender;
    private isReactMock;
    props: Readonly<{
        children?: React.ReactNode | React.ReactNode[];
    }> & Readonly<MessageModel | DefaultHtmlAttributes | MessageTypecast>;
    forceUpdate: (callBack?: () => any) => void;
    context: Object;
    portals: any;
    isReactComponent: Object;
    refs: {
        [key: string]: React.ReactInstance;
    };
    constructor(props: any);
    render(): any;
}
