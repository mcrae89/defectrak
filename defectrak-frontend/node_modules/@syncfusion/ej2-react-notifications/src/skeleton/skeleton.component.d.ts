import * as React from 'react';
import { Skeleton, SkeletonModel } from '@syncfusion/ej2-notifications';
import { DefaultHtmlAttributes } from '@syncfusion/ej2-react-base';
/**
 * Represents the React Skeleton component
 * ```html
 * <SkeletonComponent></SkeletonComponent>
 * ```
 */
export declare class SkeletonComponent extends Skeleton {
    state: Readonly<{
        children?: React.ReactNode | React.ReactNode[];
    }> & Readonly<SkeletonModel | DefaultHtmlAttributes>;
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
    }> & Readonly<SkeletonModel | DefaultHtmlAttributes>;
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
