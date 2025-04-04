import { ComplexBase, applyMixins, ComponentBase } from '@syncfusion/ej2-react-base';
import { createElement, Component } from 'react';
import { Toast, Message, Skeleton } from '@syncfusion/ej2-notifications';
export * from '@syncfusion/ej2-notifications';

/**
 * `ButtonDirective` represent a button of the react Toast.
 * It must be contained in a Toast component(`ToastrComponent`).
 * ```tsx
 * <ToastComponent>
 * <ButtonsDirective>
 * <ButtonDirective content='Ok' isPrimary=true></ButtonDirective>
 * <ButtonDirective content='Cancel'></ButtonDirective>
 * <ButtonsDirective>
 * </ToastComponent>
 * ```
 */
class ButtonModelPropDirective extends ComplexBase {
}
ButtonModelPropDirective.moduleName = 'buttonModelProp';
class ButtonModelPropsDirective extends ComplexBase {
}
ButtonModelPropsDirective.propertyName = 'buttons';
ButtonModelPropsDirective.moduleName = 'buttonModelProps';

/**
 * Represents the React Toast Component
 * ```html
 * <Toast></Toast>
 * ```
 */
class ToastComponent extends Toast {
    constructor(props) {
        super(props);
        this.initRenderCalled = false;
        this.checkInjectedModules = false;
        this.directivekeys = { 'buttonModelProps': 'buttonModelProp' };
        this.statelessTemplateProps = ["content"];
        this.templateProps = null;
        this.immediateRender = false;
        this.isReactMock = true;
        this.portals = [];
    }
    render() {
        this.isReactMock = false;
        if (((this.element && !this.initRenderCalled) || this.refreshing) && !this.isReactForeceUpdate) {
            super.render();
            this.initRenderCalled = true;
        }
        else {
            return createElement('div', this.getDefaultAttributes(), [].concat(this.props.children, this.portals));
        }
    }
}
applyMixins(ToastComponent, [ComponentBase, Component]);

/**
 * The React Message component displays messages with severity by differentiating icons and colors to denote the importance and context of the message to the end user.
 * ```html
 * <MessageComponent id='msg' showCloseIcon={true}>Editing is restricted</MessageComponent>
 * ```
 */
class MessageComponent extends Message {
    constructor(props) {
        super(props);
        this.initRenderCalled = false;
        this.checkInjectedModules = false;
        this.statelessTemplateProps = null;
        this.templateProps = null;
        this.immediateRender = false;
        this.isReactMock = true;
        this.portals = [];
    }
    render() {
        this.isReactMock = false;
        if (((this.element && !this.initRenderCalled) || this.refreshing) && !this.isReactForeceUpdate) {
            super.render();
            this.initRenderCalled = true;
        }
        else {
            return createElement('div', this.getDefaultAttributes(), [].concat(this.props.children, this.portals));
        }
    }
}
applyMixins(MessageComponent, [ComponentBase, Component]);

/**
 * Represents the React Skeleton component
 * ```html
 * <SkeletonComponent></SkeletonComponent>
 * ```
 */
class SkeletonComponent extends Skeleton {
    constructor(props) {
        super(props);
        this.initRenderCalled = false;
        this.checkInjectedModules = false;
        this.statelessTemplateProps = null;
        this.templateProps = null;
        this.immediateRender = true;
        this.isReactMock = true;
        this.portals = [];
    }
    render() {
        this.isReactMock = false;
        if (((this.element && !this.initRenderCalled) || this.refreshing) && !this.isReactForeceUpdate) {
            super.render();
            this.initRenderCalled = true;
        }
        else {
            return createElement('div', this.getDefaultAttributes(), [].concat(this.props.children, this.portals));
        }
    }
}
applyMixins(SkeletonComponent, [ComponentBase, Component]);

export { ButtonModelPropDirective, ButtonModelPropsDirective, MessageComponent, SkeletonComponent, ToastComponent };
//# sourceMappingURL=ej2-react-notifications.es2015.js.map
