function PointList_reduced = DouglasPeucker(PointList, drawdata)
%DOUGLASPEUCKER Reduce density of points in vector data using the 
%    Ramer-Douglas-Peucker algorithm. 
%   The Ramer–Douglas–Peucker algorithm is an algorithm for reducing the 
%    number of points in a curve that is approximated by a series of points.
%   References:
%    http://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm

epsilon = 1e-7;

if nargin < 2
    drawdata = false;
end

n = size(PointList,1);
PointList_reduced = RDP_recs(PointList, n, epsilon);
if drawdata
    h_f = figure('name','Ramer-Douglas-Peucker algorithm',...
        'color',[1 1 1],'menubar','none','numbertitle','off');
    h_a = axes('parent',h_f,'box','on','dataaspectratio',[1 1 1]);
    %Original data
    line(PointList(:,1),PointList(:,2),'parent',h_a,...
        'color',[1 0.5 0],'linestyle','-','linewidth',1.5,...
        'marker','o','markersize',4.5);
    %Reduced data
    line(PointList_reduced(:,1),PointList_reduced(:,2),'parent',h_a,...
        'color',[0 0 1],'linestyle','-','linewidth',2,...
        'marker','o','markersize',5);
end

    function ptList_reduced = RDP_recs(ptList, n, epsilon)
        %n = size(ptList,1);
        if n <= 2
            ptList_reduced = ptList;
            return;
        end
        %Find the point with the maximum distance
        dmax = -inf;
        idx = 0;
        for k = 2:n-1
            d = PerpendicularDistance(ptList(k,:), ptList([1,n],:));
            if d > dmax
                dmax = d;
                idx = k;
            end
        end
        %If max distance is greater than epsilon, recursively simplify
        if dmax > epsilon
            %Recursive call
            recList1 = RDP_recs(ptList(1:idx,:), idx, epsilon);
            recList2 = RDP_recs(ptList(idx:n,:), n-idx+1, epsilon);
            %Build the result list
            ptList_reduced = [recList1;recList2(2:end,:)];
        else
            ptList_reduced = ptList([1,n],:);
        end
    end

    function d = PerpendicularDistance(pt, lineNode)
        %lineNode: [NodeA[Ax,Ay];NodeB[Bx,By]]
        Ax = lineNode(1,1);
        Ay = lineNode(1,2);
        Bx = lineNode(2,1);
        By = lineNode(2,2);
        d_node = sqrt((Ax-Bx).^2+(Ay-By).^2);
        if d_node > eps
            d = abs(det([1 1 1;pt(1) Ax Bx;pt(2) Ay By]))/d_node;
        else
            d = sqrt((pt(1)-Ax).^2+(pt(2)-Ay).^2);
        end
    end

end